package rs.ac.bg.fon.eduhub.service;

import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.eduhub.dto.CreateLessonRequest;
import rs.ac.bg.fon.eduhub.dto.LessonDto;
import rs.ac.bg.fon.eduhub.dto.UpdateLessonRequest;
import rs.ac.bg.fon.eduhub.entity.impl.Course;
import rs.ac.bg.fon.eduhub.entity.impl.Lesson;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.LessonType;
import rs.ac.bg.fon.eduhub.mapper.LessonMapper;
import rs.ac.bg.fon.eduhub.repository.CourseRepository;
import rs.ac.bg.fon.eduhub.repository.LessonRepository;
import rs.ac.bg.fon.eduhub.repository.LessonTypeRepository;
import rs.ac.bg.fon.eduhub.repository.UserRepository;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonTypeRepository lessonTypeRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final LessonMapper lessonMapper;

    public LessonService(LessonRepository lessonRepository,
                         LessonTypeRepository lessonTypeRepository,
                         CourseRepository courseRepository,
                         UserRepository userRepository,
                         LessonMapper lessonMapper) {
        this.lessonRepository = lessonRepository;
        this.lessonTypeRepository = lessonTypeRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.lessonMapper = lessonMapper;
    }

    // SO15 - Pregled lekcija u okviru kursa
    public List<LessonDto> getLessonsByCourse(Long courseId) {
        findCourseOrThrow(courseId);
        return lessonRepository.findByCourse_CourseIdOrderByLessonOrderIndexAsc(courseId)
                .stream()
                .map(lessonMapper::toDto)
                .toList();
    }

    // SO12 - Dodavanje lekcije na kurs
    public LessonDto addLesson(Long courseId, CreateLessonRequest request, Authentication authentication) {
        Course course = findCourseOrThrow(courseId);
        requireCourseOwnerOrAdmin(course, authentication);

        LessonType lessonType = lessonTypeRepository.findById(request.lessonTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Lesson type not found: " + request.lessonTypeId()));

        Lesson lesson = new Lesson();
        lesson.setLessonTitle(request.lessonTitle());
        lesson.setLessonOrderIndex(request.lessonOrderIndex());
        lesson.setLessonType(lessonType);
        lesson.setIsAvailable(true);
        lesson.setCourse(course);

        lessonRepository.save(lesson);
        return lessonMapper.toDto(lesson);
    }

    // SO13 - Izmena lekcije
    public LessonDto updateLesson(Long lessonId, UpdateLessonRequest request, Authentication authentication) {
        Lesson lesson = findLessonOrThrow(lessonId);
        requireCourseOwnerOrAdmin(lesson.getCourse(), authentication);

        LessonType lessonType = lessonTypeRepository.findById(request.lessonTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Lesson type not found: " + request.lessonTypeId()));

        lesson.setLessonTitle(request.lessonTitle());
        lesson.setLessonOrderIndex(request.lessonOrderIndex());
        lesson.setLessonType(lessonType);
        lesson.setIsAvailable(request.isAvailable());

        lessonRepository.save(lesson);
        return lessonMapper.toDto(lesson);
    }

    // SO14 - Brisanje lekcije
    public void deleteLesson(Long lessonId, Authentication authentication) {
        Lesson lesson = findLessonOrThrow(lessonId);
        requireCourseOwnerOrAdmin(lesson.getCourse(), authentication);
        lessonRepository.delete(lesson);
    }

    private Course findCourseOrThrow(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));
    }

    private Lesson findLessonOrThrow(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + lessonId));
    }

    private void requireCourseOwnerOrAdmin(Course course, Authentication authentication) {
        User currentUser = userRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = course.getAuthor() != null
                && course.getAuthor().getUserId().equals(currentUser.getUserId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Only the course author or an admin can modify lessons of this course.");
        }
    }
}