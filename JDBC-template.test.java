import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

public class MyParameterizedSetterTest {

    @Test
    void testSetValues() throws SQLException {
        // Mock PreparedStatement and MyObject
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        MyObject myObject = new MyObject(/* set your object properties */);

        // Create a lambda expression for the ParameterizedPreparedStatementSetter
        MyParameterizedSetter setter = (PreparedStatement ps, MyObject obj) -> {
            ps.setString(1, obj.getProperty1());
            ps.setInt(2, obj.getProperty2());
            ps.setLong(3, obj.getId());
        };

        // Mock JdbcTemplate
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        // Create an instance of MyDAO with the mocked JdbcTemplate
        MyDAO myDAO = new MyDAO(jdbcTemplate);

        // Call the updateMyObject method
        myDAO.updateMyObject(setter, myObject);

        // Verify that the jdbcTemplate.update method was called with the correct arguments
        verify(jdbcTemplate, times(1)).update(anyString(), eq(setter), eq(myObject.getId()));

        // Verify any other assertions related to the test
    }
}
