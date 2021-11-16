package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.ImageDao;
import ar.edu.itba.paw.models.Forum;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Repository
public class ImageJdbcDao implements ImageDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<Image> ROW_MAPPER = (rs, rowNum) -> new Image(rs.getInt("image_id") , rs.getBytes("image"));

    @Autowired
    public ImageJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("images")
                .usingGeneratedKeyColumns("image_id");
    }

    @Override
    public Optional<Image> getImage(Number image_id){
        return jdbcTemplate.query("select * from images where image_id = ? " , ROW_MAPPER , image_id).stream().findFirst();
    }

    @Override
    public Image createImage(byte[] data){
        final Map<String, Object> args = new HashMap<>();
        args.put("image", data);
        final Number imageId = jdbcInsert.executeAndReturnKey(args);
        return new Image(imageId , data);
    }

}

