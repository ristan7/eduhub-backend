package rs.ac.bg.fon.eduhub.dto;

/**
 * Prenosni objekat (DTO) koji predstavlja zbirnu statistiku platforme
 * (SO24).
 *
 * @param totalUsers ukupan broj registrovanih korisnika
 * @param totalStudents broj korisnika sa ulogom STUDENT
 * @param totalInstructors broj korisnika sa ulogom INSTRUCTOR
 * @param totalAdmins broj korisnika sa ulogom ADMIN
 * @param totalCourses ukupan broj kreiranih kurseva
 * @param totalPublishedCourses broj kurseva sa statusom PUBLISHED
 * @param totalDraftCourses broj kurseva sa statusom DRAFT
 * @param totalArchivedCourses broj kurseva sa statusom ARCHIVED
 * @param totalEnrollments ukupan broj prijava studenata na kurseve
 * @param totalCertificatesIssued ukupan broj izdatih sertifikata
 * @author Mihajlo Ristanovic
 * @version 1.0
 */
public record PlatformStatisticsDto(
        long totalUsers,
        long totalStudents,
        long totalInstructors,
        long totalAdmins,
        long totalCourses,
        long totalPublishedCourses,
        long totalDraftCourses,
        long totalArchivedCourses,
        long totalEnrollments,
        long totalCertificatesIssued
) {
}