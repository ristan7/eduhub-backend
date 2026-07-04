package rs.ac.bg.fon.eduhub.dto;

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