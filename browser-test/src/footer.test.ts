import {
  createTestContext,
  enableFeatureFlag,
  disableFeatureFlag,
  validateAccessibility,
  validateScreenshot,
  TestContext,
} from './support'
import {Locator} from 'playwright'

function sharedTests(ctx: TestContext, screenshotName: string) {
  it('matches the expected screenshot', async () => {
    const footer: Locator = ctx.page.locator('footer')
    await validateScreenshot(footer, screenshotName)
  })

  it('has no accessibility violations', async () => {
    await validateAccessibility(ctx.page)
  })
}

describe('the footer', () => {
  const ctx = createTestContext()

  describe('without civiform version feature flag', () => {
    beforeEach(async () => {
      await disableFeatureFlag(
        ctx.page,
        'show_civiform_image_tag_on_landing_page',
      )
    })

    sharedTests(ctx, 'footer-no-version')

    it('does not have civiform version', async () => {
      expect(await ctx.page.textContent('html')).not.toContain(
        'CiviForm version:',
      )
    })
  })

  describe('with civiform version feature flag', () => {
    beforeEach(async () => {
      await enableFeatureFlag(
        ctx.page,
        'show_civiform_image_tag_on_landing_page',
      )
    })

    sharedTests(ctx, 'footer-with-version')

    it('has civiform version', async () => {
      expect(await ctx.page.textContent('html')).toContain('CiviForm version:')
    })
  })
})
