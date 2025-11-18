package kim.onbidproperty.config;

import kim.onbidproperty.enums.PurchaseStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(PurchaseStatus.class)
public class PurchaseStatusTypeHandler extends BaseTypeHandler<PurchaseStatus> {
    @Override
    public void setNonNullParameter (PreparedStatement ps, int i,
                                    PurchaseStatus parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public PurchaseStatus getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : PurchaseStatus.from(value);
    }

    @Override
    public PurchaseStatus getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : PurchaseStatus.from(value);
    }

    @Override
    public PurchaseStatus getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : PurchaseStatus.from(value);
    }
}
