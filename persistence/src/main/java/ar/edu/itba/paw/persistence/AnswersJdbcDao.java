package ar.edu.itba.paw.persistence;


/*
@Primary
@Repository

public class AnswersJdbcDao implements AnswersDao {

    private final JdbcTemplate jdbcTemplate; // comunicación base de datos.
    private final SimpleJdbcInsert jdbcInsert;
    private final SimpleJdbcInsert jdbcInsertVotes;

    private final static RowMapper<Answer> ROW_MAPPER = (rs, rowNum) -> new Answer(
            rs.getLong("answer_id"),
            rs.getString("body"),
            rs.getBoolean("verify"),
            rs.getLong("question_id"),
            rs.getInt("votes"),
            new User(rs.getLong("user_id"), rs.getString("user_name"), rs.getString("user_email"), rs.getString("user_password"))
            );

    private final static String MAPPED_QUERY = "select votes, answer.answer_id, body, verify, question_id, users.user_id, users.username AS user_name, users.email AS user_email, users.password AS user_password " +
            "from answer JOIN users ON answer.user_id = users.user_id left join (Select answer.answer_id, sum(case when vote = true then 1 when vote = false then -1 else 0 end) as votes " +
            "from answer left join answervotes as a on answer.answer_id = a.answer_id group by answer.answer_id) votes on votes.answer_id = answer.answer_id ";


    private final static RowMapper<Long> COUNT_ROW_MAPPER = (rs, rowNum) -> rs.getLong("count");

   @Autowired
    public AnswersJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("answer")
                .usingGeneratedKeyColumns("answer_id" , "time");
       jdbcInsertVotes = new SimpleJdbcInsert(jdbcTemplate)
               .withTableName("answervotes")
               .usingGeneratedKeyColumns("votes_id");
    }



    @Override
    public Optional<Answer> findById(long id ){
        final List<Answer> list = jdbcTemplate.query(
                "Select votes, answer.answer_id, body, verify, question_id, users.user_id, users.username AS user_name, users.email AS user_email, users.password AS user_password\n" +
                        "from answer JOIN users ON answer.user_id = users.user_id left join (Select answer.answer_id, sum(case when vote = true then 1 when vote = false then -1 end) as votes\n" +
                        "from answer left join answervotes as a on answer.answer_id = a.answer_id group by answer.answer_id) votes on votes.answer_id = answer.answer_id where votes.answer_id = ?", ROW_MAPPER, id);

        return list.stream().findFirst();
    }

    @Override
    public List<Answer> findByQuestion(long question, int limit, int offset) {
        final List<Answer> list = jdbcTemplate.query(
                "Select votes, answer.answer_id, body, verify, question_id, users.user_id, users.username AS user_name, users.email AS user_email, users.password AS user_password " +
                        "from answer JOIN users ON answer.user_id = users.user_id left join (Select answer.answer_id, sum(case when vote = true then 1 when vote = false then -1 else 0 end) as votes " +
                        "from answer left join answervotes as a on answer.answer_id = a.answer_id group by answer.answer_id) votes on votes.answer_id = answer.answer_id where question_id = ? order by (case when verify = true then 1 else 2 end),votes desc, answer.answer_id limit ? offset ?", ROW_MAPPER, question, limit, offset);

        return list;
    }



    @Override
    public Answer create( String body ,User owner, Long question) {
        final Map<String, Object> args = new HashMap<>();
        args.put("body", body);
        args.put("user_id" , owner.getId());
        args.put("question_id" , question);
        args.put("verify",null);
        final Map<String, Object> keys = jdbcInsert.executeAndReturnKeyHolder(args).getKeys();
        Long id = ((Integer) keys.get("answer_id")).longValue();
        return new Answer(id,body,null,  question,owner);
    }

    @Override
    public Optional<Answer> verify(Long id, boolean bool){
       jdbcTemplate.update("update answer set verify = ? where answer_id = ?",bool, id);
       return findById(id);
    }

    @Override
    public void addVote(Boolean vote, Long user, Long answerId) {
        Optional<Long> voteId = jdbcTemplate.query("select votes_id from answervotes where answer_id=? AND user_id= ?", (rs, row) -> rs.getLong("votes_id"),answerId,user).stream().findFirst();
        if(!voteId.isPresent()) {
            final Map<String, Object> args = new HashMap<>();
            args.put("user_id", user);
            args.put("vote", vote);
            args.put("answer_id", answerId);
            jdbcInsertVotes.executeAndReturnKeyHolder(args).getKeys();
            return;
        }
        jdbcTemplate.update("update answervotes set vote = ?, user_id = ?, answer_id = ? where votes_id=?",vote, user, answerId, voteId.get() );
   }

    @Override
    public Optional<Long> countAnswers(long question) {
        Optional<Long> count = jdbcTemplate.query("Select count(distinct answer.answer_id) from answer where question_id = ?", (rs, row) -> rs.getLong("count"), question).stream().findFirst();
        return count;
   }
    @Override
    public List<Answer> findByUser(long userId, int offset, int limit) {
        return jdbcTemplate.query(MAPPED_QUERY + "WHERE answer.user_id = ? order by answer_id desc offset ? limit ? ", ROW_MAPPER, userId, offset, limit);
    }

    @Override
    public int findByUserCount(long userId){
       return jdbcTemplate.query("SELECT COUNT(*) AS count FROM answer WHERE user_id = ?", COUNT_ROW_MAPPER, userId).stream().findFirst().get().intValue();
    }


}

 */
