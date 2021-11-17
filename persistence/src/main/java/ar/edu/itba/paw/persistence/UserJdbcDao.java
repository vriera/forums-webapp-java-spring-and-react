package ar.edu.itba.paw.persistence;

/*
@Repository
public class UserJdbcDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<User> ROW_MAPPER = (rs, rowNum) -> new User(rs.getLong("user_id"), rs.getString("username"), rs.getString("email"), rs.getString("password"));
    private final static RowMapper<Notification> NOTIFICATIONS_ROW_MAPPER = (rs, rowNum) -> new Notification(
            new User ( rs.getLong("user_id") , rs.getString("email") , rs.getString("password") , rs.getString("username"))
            , rs.getLong("total") , rs.getLong("requests") , rs.getLong("invites") );
    @Autowired
    public UserJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
    }


    @Override
    public List<User> list() {
        return jdbcTemplate.query("SELECT * FROM users", ROW_MAPPER);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        final List<User> list = jdbcTemplate.query("SELECT * FROM users WHERE email = ?", ROW_MAPPER, email);
        return list.stream().findFirst();
    }

    @Override
    public Optional<User> findById(long id){
        final List<User> list = jdbcTemplate.query("SELECT * FROM users WHERE user_id = ?", ROW_MAPPER, id);
        return list.stream().findFirst();
    }

    @Override
    public User create(final String username, final String email, final String password) {
        final Map<String, Object> args = new HashMap<>();
        args.put("username", username);
        args.put("email", email);
        args.put("password", password);
        final Number userId = jdbcInsert.executeAndReturnKey(args);
        return new User(userId.longValue(), username, email, password);
    }

    @Override
    public Optional<User> updateCredentials(User user, String newUsername, String newPassword) {
        return Optional.empty();
    }


    @Override
    public Optional<User> updateCredentials(Number id, String newUsername, String newPassword) {
        final List<User> list = jdbcTemplate.query("UPDATE users SET username = ?, password = ? WHERE user_id = ? RETURNING * ", ROW_MAPPER, newUsername, newPassword, id.longValue());
        return list.stream().findFirst();
    }




    private static final String ACCESS_MAPPED_QUERY = "SELECT users.user_id as user_id, users.username as username, users.email as email, users.password as password FROM access JOIN users on access.user_id = users.user_id ";

    @Override
    public List<User> getMembersByAccessType(Number communityId, AccessType type, long offset, long limit) {
        if(type == null)
            return jdbcTemplate.query(ACCESS_MAPPED_QUERY + "WHERE community_id = ? order by access_id desc offset ? limit ?", ROW_MAPPER, communityId.longValue(), offset, limit);

        return jdbcTemplate.query(ACCESS_MAPPED_QUERY + "WHERE community_id = ? and access_type = ? order by access_id desc offset ? limit ?", ROW_MAPPER, communityId.longValue(), type.ordinal(), offset, limit);
    }

    private final static RowMapper<Long> COUNT_ROW_MAPPER = (rs, rowNum) -> rs.getLong("count");

    @Override
    public long getMemberByAccessTypeCount(Number communityId, AccessType type) {

        if(type==null)
            //nunca debería fallar, ya que el count siempre devuelve algo
            return jdbcTemplate.query("SELECT COUNT(*) as count FROM access where community_id = ?", COUNT_ROW_MAPPER, communityId.longValue()).stream().findFirst().orElseThrow(NoSuchFieldError::new);

        return jdbcTemplate.query("SELECT COUNT(*) as count FROM access where community_id = ? and access_type = ?", COUNT_ROW_MAPPER, communityId.longValue(), type.ordinal()).stream().findFirst().orElseThrow(NoSuchFieldError::new);
    }

    @Override
    public Optional<Notification> getNotifications(Number userId){
        return jdbcTemplate.query("SELECT * from users natural join notifications where user_Id = ?" , NOTIFICATIONS_ROW_MAPPER , userId).stream().findFirst();
    }
    @Override
    public Optional<Karma> getKarma(Number userId){
        return Optional.empty();
    }
}
  */