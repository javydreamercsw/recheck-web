package de.retest.web;

import static de.retest.recheck.ui.image.ImageUtils.extractScale;
import static de.retest.recheck.ui.image.ImageUtils.resizeImage;

import java.awt.image.BufferedImage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.assertthat.selenium_shutterbug.core.Shutterbug;
import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy;

import de.retest.web.selenium.UnbreakableDriver;

public class ScreenshotProvider {

	private static final String VIEWPORT_ONLY_SCREENSHOT_PROPERTY = "de.retest.recheck.web.viewportOnlyScreenshot";
	private static final int SCROLL_TIMEOUT_MS = 100;
	private static final boolean USE_DEVICE_PIXEL_RATIO = true;

	public static final int SCALE = extractScale();

	private ScreenshotProvider() {}

	public static BufferedImage shoot( final WebDriver driver ) {
		final boolean viewportOnly = Boolean.getBoolean( VIEWPORT_ONLY_SCREENSHOT_PROPERTY );
		final WebDriver wrapped = unwrap( driver );
		return viewportOnly ? shootViewportOnly( wrapped ) : shootFullPage( wrapped );
	}

	private static BufferedImage shootFullPage( final WebDriver driver ) {
		final BufferedImage image = Shutterbug
				.shootPage( driver, ScrollStrategy.WHOLE_PAGE, SCROLL_TIMEOUT_MS, USE_DEVICE_PIXEL_RATIO ).getImage();
		return driver instanceof ChromeDriver
				? resizeImage( image, image.getWidth() / SCALE, image.getHeight() / SCALE ) : image;
	}

	private static BufferedImage shootViewportOnly( final WebDriver driver ) {
		return Shutterbug.shootPage( driver, USE_DEVICE_PIXEL_RATIO ).getImage();
	}

	/**
	 * Unwrap the actual {@code WebDriver} if present. Since Shutterbug uses {@code instanceof} checks, these would fail
	 * in the case of {@code UnbreakableDriver}.
	 *
	 * @param driver
	 *            The driver to unwrap.
	 * @return The wrapped driver if present, otherwise the given driver.
	 */
	private static WebDriver unwrap( final WebDriver driver ) {
		return driver instanceof UnbreakableDriver ? ((UnbreakableDriver) driver).getWrapped() : driver;
	}

}
