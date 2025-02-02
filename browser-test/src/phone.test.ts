import {
  createTestContext,
  loginAsAdmin,
  logout,
  validateAccessibility,
  validateScreenshot,
} from './support'

describe('phone question for applicant flow', () => {
  const ctx = createTestContext(/* clearDb= */ false)

  describe('single phone question', () => {
    const programName = 'Test program for single phone q'

    beforeAll(async () => {
      const {page, adminQuestions, adminPrograms} = ctx
      // As admin, create program with a free form text question.
      await loginAsAdmin(page)

      await adminQuestions.addPhoneQuestion({
        questionName: 'phone-q',
      })
      await adminPrograms.addAndPublishProgramWithQuestions(
        ['phone-q'],
        programName,
      )

      await logout(page)
    })

    it('validate screenshot', async () => {
      const {page, applicantQuestions} = ctx
      await applicantQuestions.applyProgram(programName)

      await validateScreenshot(page, 'phone')
    })

    it('validate screenshot with errors', async () => {
      const {page, applicantQuestions} = ctx
      await applicantQuestions.applyProgram(programName)
      await applicantQuestions.clickNext()

      await validateScreenshot(page, 'phone-errors')
    })

    it('with phone submits successfully', async () => {
      const {page, applicantQuestions} = ctx
      await applicantQuestions.applyProgram(programName)
      await applicantQuestions.answerPhoneQuestion(
        'United States',
        '4256373270',
      )
      await validateScreenshot(page, 'phone-format-usa')
      await applicantQuestions.clickNext()

      await applicantQuestions.submitFromReviewPage()
    })
    it('with canada phone submits successfully', async () => {
      const {page, applicantQuestions} = ctx
      await applicantQuestions.applyProgram(programName)
      await applicantQuestions.answerPhoneQuestion('Canada', '2507274212')

      await validateScreenshot(page, 'phone-format-ca')

      await applicantQuestions.clickNext()
      await applicantQuestions.submitFromReviewPage()
    })

    it('with empty phone does not submit', async () => {
      const {page, applicantQuestions} = ctx
      await applicantQuestions.applyProgram(programName)

      // Click next without inputting anything
      await applicantQuestions.clickNext()

      const textId = '.cf-question-phone'
      expect(await page.innerText(textId)).toContain(
        'This question is required.',
      )
    })
    it('with empty country code does not submit', async () => {
      const {page, applicantQuestions} = ctx
      await applicantQuestions.applyProgram(programName)

      // Click next without inputting anything
      await applicantQuestions.clickNext()

      const countryCodeId = '.cf-phone-country-code'
      expect(await page.innerText(countryCodeId)).toContain(
        'Country must be selected',
      )
    })

    it('invalid phone numbers', async () => {
      const {page, applicantQuestions} = ctx
      await applicantQuestions.applyProgram(programName)
      await applicantQuestions.answerPhoneQuestion(
        'United States',
        '1234567890',
      )

      await applicantQuestions.clickNext()

      const countryCodeId = '.cf-phone-number'
      expect(await page.innerText(countryCodeId)).toContain(
        'This phone number is invalid',
      )
    })

    it('invalid phone numbers for the country', async () => {
      const {page, applicantQuestions} = ctx
      await applicantQuestions.applyProgram(programName)
      await applicantQuestions.answerPhoneQuestion(
        'United States',
        '2507274212.',
      )

      await applicantQuestions.clickNext()

      const countryCodeId = '.cf-phone-number'
      expect(await page.innerText(countryCodeId)).toContain(
        'The phone you have provided does not belong to the country',
      )
    })

    it('555 fake phone numbers', async () => {
      const {page, applicantQuestions} = ctx
      await applicantQuestions.applyProgram(programName)
      await applicantQuestions.answerPhoneQuestion(
        'United States',
        '5553231234',
      )

      await applicantQuestions.clickNext()
      const countryCodeId = '.cf-phone-number'
      expect(await page.innerText(countryCodeId)).toContain(
        'This phone number is invalid',
      )
    })
    it('invalid characters in phone numbers', async () => {
      const {page, applicantQuestions} = ctx
      await applicantQuestions.applyProgram(programName)
      await applicantQuestions.answerPhoneQuestion(
        'United States',
        '123###1212',
      )

      await applicantQuestions.clickNext()
      const countryCodeId = '.cf-phone-number'
      expect(await page.innerText(countryCodeId)).toContain(
        'This phone number is invalid',
      )
    })
    it('incorrect length of phone number', async () => {
      const {page, applicantQuestions} = ctx
      await applicantQuestions.applyProgram(programName)
      await applicantQuestions.answerPhoneQuestion('United States', '615974')

      await applicantQuestions.clickNext()
      const countryCodeId = '.cf-phone-number'
      expect(await page.innerText(countryCodeId)).toContain(
        'Phone number is required',
      )
    })
    it('hitting enter on phone does not trigger submission', async () => {
      const {page, applicantQuestions} = ctx
      await applicantQuestions.applyProgram(programName)
      await applicantQuestions.answerPhoneQuestion('Canada', '2507274212.')

      // Ensure that clicking enter while on phone input doesn't trigger form
      // submission.
      await page.focus('input[type=text]')
      await page.keyboard.press('Enter')
      expect(await page.locator('input[type=text]').isVisible()).toEqual(true)

      // Check that pressing Enter on button works.
      await page.focus('button:has-text("Save and next")')
      await page.keyboard.press('Enter')
      await applicantQuestions.expectReviewPage()

      // Go back to question and ensure that "Review" button is also clickable
      // via Enter.
      await page.click('a:has-text("Edit")')
      await page.focus('a:has-text("Review")')
      await page.keyboard.press('Enter')
      await applicantQuestions.expectReviewPage()
    })
    it('has no accessiblity violations', async () => {
      const {page, applicantQuestions} = ctx
      await applicantQuestions.applyProgram(programName)

      await validateAccessibility(page)
    })
  })

  describe('multiple phone questions', () => {
    const programName = 'Test program for multiple phone qs'

    beforeAll(async () => {
      const {page, adminQuestions, adminPrograms} = ctx
      await loginAsAdmin(page)

      await adminQuestions.addPhoneQuestion({
        questionName: 'first-phone-q',
      })
      await adminQuestions.addPhoneQuestion({
        questionName: 'second-phone-q',
      })

      await adminPrograms.addProgram(programName)
      await adminPrograms.editProgramBlockWithOptional(
        programName,
        'Optional question block',
        ['second-phone-q'],
        'first-phone-q', // optional
      )
      await adminPrograms.publishAllDrafts()

      await logout(page)
    })

    it('with both selections submits successfully', async () => {
      const {applicantQuestions} = ctx
      await applicantQuestions.applyProgram(programName)
      await applicantQuestions.answerPhoneQuestion('Canada', '2507274212', 0)
      await applicantQuestions.answerPhoneQuestion(
        'United States',
        '4256373270',
        1,
      )
      await applicantQuestions.clickNext()

      await applicantQuestions.submitFromReviewPage()
    })

    it('with unanswered optional question submits successfully', async () => {
      const {applicantQuestions} = ctx
      // Only answer second question. First is optional.
      await applicantQuestions.applyProgram(programName)
      await applicantQuestions.answerPhoneQuestion(
        'United States',
        '4256373270',
        1,
      )
      await applicantQuestions.clickNext()

      await applicantQuestions.submitFromReviewPage()
    })

    it('with first invalid does not submit', async () => {
      const {page, applicantQuestions} = ctx
      await applicantQuestions.applyProgram(programName)
      await applicantQuestions.answerPhoneQuestion(
        'United States',
        '1234567320',
        0,
      )
      await applicantQuestions.answerPhoneQuestion(
        'United States',
        '4256373270',
        1,
      )
      await applicantQuestions.clickNext()

      const textId = '.cf-question-phone'
      expect(await page.innerText(textId)).toContain(
        'This phone number is invalid',
      )
    })

    it('with second invalid does not submit', async () => {
      const {page, applicantQuestions} = ctx
      await applicantQuestions.applyProgram(programName)
      await applicantQuestions.answerPhoneQuestion(
        'United States',
        '4256373270',
        0,
      )
      await applicantQuestions.answerPhoneQuestion(
        'United States',
        '1234567320',
        1,
      )
      await applicantQuestions.clickNext()

      const textId = `.cf-question-phone >> nth=1`
      expect(await page.innerText(textId)).toContain(
        'This phone number is invalid',
      )
    })
  })
})
