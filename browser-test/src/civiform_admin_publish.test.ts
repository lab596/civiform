import {
  AdminPrograms,
  AdminQuestions,
  dismissModal,
  dropTables,
  gotoEndpoint,
  loginAsAdmin,
  startSession,
  validateScreenshot,
} from './support'
import {Page} from 'playwright'
import {ProgramVisibility} from './support/admin_programs'

describe('publishing all draft questions and programs', () => {
  let pageObject: Page
  let adminPrograms: AdminPrograms
  let adminQuestions: AdminQuestions

  const hiddenProgramNoQuestions = 'Public test program hidden no questions'
  const visibleProgramWithQuestion = 'Public test program visible with question'
  const questionName = 'publish-test-address-q'
  const questionText = 'publish-test-address-q'
  // CreateNewVersion implicitly updates the question text to be suffixed with " new version".
  const draftQuestionText = `${questionText} new version`

  beforeAll(async () => {
    const session = await startSession()
    pageObject = session.page

    await dropTables(pageObject)
    await gotoEndpoint(pageObject)

    adminPrograms = new AdminPrograms(pageObject)
    adminQuestions = new AdminQuestions(pageObject)

    await loginAsAdmin(pageObject)

    // Create a hidden program with no questions
    await adminPrograms.addProgram(
      hiddenProgramNoQuestions,
      'program description',
      'https://usa.gov',
      ProgramVisibility.HIDDEN,
    )

    // Create a new question referenced by a program.
    await adminQuestions.addAddressQuestion({questionName, questionText})
    await adminPrograms.addProgram(visibleProgramWithQuestion)
    await adminPrograms.editProgramBlock(
      visibleProgramWithQuestion,
      'dummy description',
      [questionName],
    )

    // Publish.
    await adminPrograms.publishAllDrafts()

    // Make an edit to the program with no questions.
    await adminPrograms.createNewVersion(hiddenProgramNoQuestions)

    // Make an edit to the shared question.
    await adminQuestions.createNewVersion(questionName)

    await adminPrograms.gotoAdminProgramsPage()
  })

  it('shows programs and questions that will be published in the modal', async () => {
    await adminPrograms.expectProgramReferencesModalContains({
      expectedQuestionsContents: [`${draftQuestionText} - Edit`],
      expectedProgramsContents: [
        `${hiddenProgramNoQuestions} - Hidden from applicants Edit`,
        `${visibleProgramWithQuestion} - Publicly visible Edit`,
      ],
    })
  })

  it('validate screenshot', async () => {
    await adminPrograms.openPublishAllDraftsModal()
    await validateScreenshot(
      adminPrograms.publishAllProgramsModalLocator(),
      'publish-modal',
    )
    await dismissModal(pageObject)
  })
})
