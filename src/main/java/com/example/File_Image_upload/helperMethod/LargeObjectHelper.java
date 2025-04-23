package com.example.File_Image_upload.helperMethod;

import lombok.RequiredArgsConstructor;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class LargeObjectHelper {


    private final JdbcTemplate jdbcTemplate;

    public byte[] read(Long oid) throws SQLException {
        return jdbcTemplate.execute((Connection con) -> {
            LargeObjectManager lobj = con.unwrap(org.postgresql.PGConnection.class).getLargeObjectAPI();
            LargeObject obj = lobj.open(oid, LargeObjectManager.READ);
            byte[] data = obj.read((int) obj.size());
            obj.close();
            return data;
        });
    }
}

