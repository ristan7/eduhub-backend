package rs.ac.bg.fon.eduhub.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rs.ac.bg.fon.eduhub.dto.UserDto;
import rs.ac.bg.fon.eduhub.entity.impl.User;
import rs.ac.bg.fon.eduhub.entity.lookup.Role;

/**
 * JUnit testovi za {@link UserMapper}.
 *
 * @author Mihajlo Ristanovic
 */
class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void testToDtoMapsAllFields() {
        User user = new User();
        user.setUserId(1L);
        user.setUserEmail("student@eduhub.com");
        user.setFirstName("Petar");
        user.setLastName("Nikolic");
        user.setIsActive(true);
        user.setRole(new Role(1L, "STUDENT"));

        UserDto dto = userMapper.toDto(user);

        assertEquals(1L, dto.userId());
        assertEquals("student@eduhub.com", dto.userEmail());
        assertEquals("Petar", dto.firstName());
        assertEquals("Nikolic", dto.lastName());
        assertEquals("STUDENT", dto.roleName());
        assertEquals(true, dto.isActive());
    }

    @Test
    void testToDtoWithNullRole() {
        User user = new User();
        user.setUserId(1L);
        user.setRole(null);

        UserDto dto = userMapper.toDto(user);

        assertNull(dto.roleName());
    }

    @Test
    void testToDtoWithNullUserReturnsNull() {
        assertNull(userMapper.toDto(null));
    }
}