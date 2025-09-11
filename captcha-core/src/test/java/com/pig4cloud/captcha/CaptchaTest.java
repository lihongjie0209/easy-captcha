package com.pig4cloud.captcha;

import com.pig4cloud.captcha.model.CharacterBoundingBox;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

/**
 * 测试类 Created by 王帆 on 2018-07-27 上午 10:08.
 */
@Slf4j
public class CaptchaTest {
	private static final Integer DEFAULT_IMAGE_WIDTH = 100;

	private static final Integer DEFAULT_IMAGE_HEIGHT = 40;
	@Test
	public void testMath() throws FileNotFoundException {
		for (int i = 0; i < 100; i++) {
			ArithmeticCaptcha captcha = new ArithmeticCaptcha(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
			log.info(captcha.text());
			captcha.out(new FileOutputStream(getPath(+i + "-math.png")));
		}
	}
	@Test
	public void test() throws Exception {
		for (int i = 0; i < 1; i++) {
			SpecCaptcha specCaptcha = new SpecCaptcha();
			specCaptcha.setLen(4);
			specCaptcha.setFont(i, 32f);
			log.info(specCaptcha.text());
			specCaptcha.out(new FileOutputStream(getPath(+i + "1.png")));
		}
	}

	@Test
	public void testGIf() throws Exception {
		for (int i = 0; i < 1; i++) {
			GifCaptcha gifCaptcha = new GifCaptcha();
			gifCaptcha.setLen(5);
			gifCaptcha.setFont(i, 32f);
			log.info(gifCaptcha.text());
			gifCaptcha.out(new FileOutputStream(getPath(+i + "2.gif")));
		}
	}

	@Test
	public void testHan() throws Exception {
		for (int i = 0; i < 1; i++) {
			ChineseCaptcha chineseCaptcha = new ChineseCaptcha();
			log.info(chineseCaptcha.text());
			chineseCaptcha.out(new FileOutputStream(getPath(+i + "3.png")));
		}
	}

	@Test
	public void testGifHan() throws Exception {
		for (int i = 0; i < 1; i++) {
			ChineseGifCaptcha chineseGifCaptcha = new ChineseGifCaptcha();
			log.info(chineseGifCaptcha.text());
			chineseGifCaptcha.out(new FileOutputStream(getPath(+i + "4.gif")));
		}
	}

	@Test
	public void testArit() throws Exception {
		for (int i = 0; i < 1; i++) {
			ArithmeticCaptcha specCaptcha = new ArithmeticCaptcha();
			specCaptcha.setLen(3);
			specCaptcha.supportAlgorithmSign(2);
			specCaptcha.setDifficulty(50);
			specCaptcha.setFont(i, 28f);
			log.info(specCaptcha.getArithmeticString() + " " + specCaptcha.text());
			specCaptcha.out(new FileOutputStream(getPath(+i + "5.png")));
		}
	}

	@Test
	public void testBase64() throws Exception {
		GifCaptcha specCaptcha = new GifCaptcha();
		log.info(specCaptcha.toBase64(""));
	}

	@Test
	public void testCharacterBoundingBoxes() throws Exception {
		SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);
		List<CharacterBoundingBox> boundingBoxes = captcha.getCharacterBoundingBoxes();
		
		log.info("验证码文本: " + captcha.text());
		log.info("字符边界框信息:");
		for (CharacterBoundingBox bbox : boundingBoxes) {
			log.info(bbox.toString());
		}
		
		// 验证返回的边界框数量与验证码长度一致
		assert boundingBoxes.size() == captcha.getLen();
		
		// 验证每个边界框都有有效的坐标和尺寸
		for (CharacterBoundingBox bbox : boundingBoxes) {
			assert bbox.getX() >= 0;
			assert bbox.getY() >= 0;
			assert bbox.getWidth() > 0;
			assert bbox.getHeight() > 0;
			assert bbox.getCharacter() != 0; // 确保字符不为空
		}
	}

	@Test
	public void testOutWithBoundingBoxes() throws Exception {
		SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);
		List<CharacterBoundingBox> boundingBoxes = captcha.outWithBoundingBoxes(
			new FileOutputStream(getPath("bbox-test.png"))
		);
		
		log.info("生成验证码文本: " + captcha.text());
		log.info("生成的字符边界框信息:");
		for (CharacterBoundingBox bbox : boundingBoxes) {
			log.info(bbox.toString());
		}
		
		// 验证返回的边界框数量与验证码长度一致
		assert boundingBoxes.size() == captcha.getLen();
		
		// 验证每个边界框都有有效的坐标和尺寸
		for (CharacterBoundingBox bbox : boundingBoxes) {
			assert bbox.getX() >= 0;
			assert bbox.getY() >= 0;
			assert bbox.getWidth() > 0;
			assert bbox.getHeight() > 0;
			assert bbox.getCharacter() != 0; // 确保字符不为空
		}
	}

	private static String getPath(String name) {
		URL resource = CaptchaTest.class.getResource("");
		try {
			return Paths.get(resource.toURI())
					.toFile()
					.getAbsolutePath()
					.split("test-classes")[0]
					+ name;
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
