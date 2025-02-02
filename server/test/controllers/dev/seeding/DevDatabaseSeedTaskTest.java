package controllers.dev.seeding;

import static controllers.dev.seeding.SampleQuestionDefinitions.ADDRESS_QUESTION_DEFINITION;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.Set;
import models.Question;
import org.junit.Before;
import org.junit.Test;
import repository.ProgramRepository;
import repository.QuestionRepository;
import repository.ResetPostgres;
import services.question.QuestionService;
import services.question.types.QuestionDefinition;

public class DevDatabaseSeedTaskTest extends ResetPostgres {

  private QuestionRepository questionRepository;
  private ProgramRepository programRepository;
  private DevDatabaseSeedTask devDatabaseSeedTask;

  @Before
  public void setUp() {
    questionRepository = instanceOf(QuestionRepository.class);
    programRepository = instanceOf(ProgramRepository.class);
    devDatabaseSeedTask = instanceOf(DevDatabaseSeedTask.class);
  }

  @Test
  public void seedQuestions_whenQuestionsNotSeededYet_itSeedsTheQuestions() throws Exception {
    assertThat(getAllQuestions().size()).isEqualTo(0);

    devDatabaseSeedTask.seedQuestions();

    // All questions from the sample questions file are seeded.
    assertThat(getAllQuestions().size())
        .isEqualTo(SampleQuestionDefinitions.ALL_SAMPLE_QUESTION_DEFINITIONS.size());
  }

  @Test
  public void seedQuestions_whenSomeQuestionsAlreadySeeded_itSeedsTheMissingOnes() {
    instanceOf(QuestionService.class).create(ADDRESS_QUESTION_DEFINITION);
    assertThat(getAllQuestions().size()).isEqualTo(1);

    // Seeding should be idempotent and skip the already seeded ADDRESS_QUESTION_DEFINITION.
    devDatabaseSeedTask.seedQuestions();

    assertThat(getAllQuestions().size())
        .isEqualTo(SampleQuestionDefinitions.ALL_SAMPLE_QUESTION_DEFINITIONS.size());
  }

  @Test
  public void insertPrograms_seedsPrograms() {
    ImmutableList<QuestionDefinition> seededQuestions = devDatabaseSeedTask.seedQuestions();

    devDatabaseSeedTask.insertMinimalSampleProgram(seededQuestions);
    devDatabaseSeedTask.insertComprehensiveSampleProgram(seededQuestions);

    assertThat(programRepository.getAllProgramNames())
        .containsExactlyInAnyOrder("comprehensive-sample-program", "minimal-sample-program");
  }

  private Set<Question> getAllQuestions() {
    return questionRepository.listQuestions().toCompletableFuture().join();
  }
}
