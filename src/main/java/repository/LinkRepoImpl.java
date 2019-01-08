package repository;

import model.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class LinkRepoImpl implements LinkRepo
{
    private static final BeanPropertyRowMapper<Link> ROW_MAPPER =
            BeanPropertyRowMapper.newInstance(Link.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertLink;

    @Autowired
    public LinkRepoImpl(DataSource dataSource, JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertLink = new SimpleJdbcInsert(dataSource)
                .withTableName("links").
                        usingColumns("original", "link")
                         .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Link getLinkById(int linkId)
    {
        return DataAccessUtils.singleResult(jdbcTemplate.query("SELECT * FROM links WHERE id=?",
                ROW_MAPPER, linkId));
    }

    @Override
    public Link getLinkByShortName(String shortName)
    {
        return DataAccessUtils.singleResult(jdbcTemplate.query("SELECT * FROM links WHERE link=?",
                ROW_MAPPER, shortName));
    }

    @Override
    @Transactional
    public Link saveLink(Link link)
    {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(link);

        if (link.getId() == null) // new link
        {
            Number newKey = insertLink.executeAndReturnKey(parameterSource);
            link.setId(newKey.intValue());
        } else {
            namedParameterJdbcTemplate.update(
                    "UPDATE links SET count=:count WHERE id=:id", parameterSource);
        }
        return link;
    }

    @Override
    public List<Link> getAllLinks()
    {
        return jdbcTemplate.query("SELECT * FROM links ORDER BY count DESC",
                ROW_MAPPER);
    }
}
