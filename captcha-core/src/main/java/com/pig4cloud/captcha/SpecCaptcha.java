package com.pig4cloud.captcha;

import com.pig4cloud.captcha.base.Captcha;
import com.pig4cloud.captcha.model.CharacterBoundingBox;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * png格式验证码 Created by 王帆 on 2018-07-27 上午 10:08.
 */
@Slf4j
public class SpecCaptcha extends Captcha {

	public SpecCaptcha() {
	}

	public SpecCaptcha(int width, int height) {
		this();
		setWidth(width);
		setHeight(height);
	}

	public SpecCaptcha(int width, int height, int len) {
		this(width, height);
		setLen(len);
	}

	public SpecCaptcha(int width, int height, int len, Font font) {
		this(width, height, len);
		setFont(font);
	}

	/**
	 * 生成验证码
	 * @param out 输出流
	 * @return 是否成功
	 */
	@Override
	public boolean out(OutputStream out) {
		return graphicsImage(textChar(), out);
	}

	@Override
	public String toBase64() {
		return toBase64("data:image/png;base64,");
	}

	@Override
	public String getContentType() {
		return "image/png";
	}

	/**
	 * 生成验证码图形
	 * @param strs 验证码
	 * @param out 输出流
	 * @return boolean
	 */
	private boolean graphicsImage(char[] strs, OutputStream out) {
		BufferedImage bi = null;
		try {
			bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = (Graphics2D) bi.getGraphics();
			// 填充背景
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, width, height);
			// 抗锯齿
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			// 画干扰圆
			drawOval(2, g2d);
			// 画干扰线
			g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
			drawBesselLine(1, g2d);
			// 画字符串
			g2d.setFont(getFont());
			FontMetrics fontMetrics = g2d.getFontMetrics();
			int fW = width / strs.length; // 每一个字符所占的宽度
			int fSp = (fW - (int) fontMetrics.getStringBounds("W", g2d).getWidth()) / 2; // 字符的左右边距
			for (int i = 0; i < strs.length; i++) {
				g2d.setColor(color());
				int fY = height
						- ((height - (int) fontMetrics.getStringBounds(String.valueOf(strs[i]), g2d).getHeight()) >> 1); // 文字的纵坐标
				g2d.drawString(String.valueOf(strs[i]), i * fW + fSp + 3, fY - 3);
			}
			g2d.dispose();
			ImageIO.write(bi, "png", out);
			out.flush();
			return true;
		}
		catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		finally {
			try {
				out.close();
			}
			catch (IOException e) {
				log.error(e.getMessage(), e);
			}

			if (bi != null) {
				bi.getGraphics().dispose();
			}
		}
		return false;
	}

	/**
	 * 获取验证码字符及对应的边界框信息，用于机器学习标记和训练
	 * @return 字符边界框信息列表
	 */
	public List<CharacterBoundingBox> getCharacterBoundingBoxes() {
		char[] strs = textChar();
		List<CharacterBoundingBox> boundingBoxes = new ArrayList<>();
		
		// 创建临时的BufferedImage来计算字符位置
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) bi.getGraphics();
		g2d.setFont(getFont());
		FontMetrics fontMetrics = g2d.getFontMetrics();
		
		int fW = width / strs.length; // 每一个字符所占的宽度
		int fSp = (fW - (int) fontMetrics.getStringBounds("W", g2d).getWidth()) / 2; // 字符的左右边距
		
		for (int i = 0; i < strs.length; i++) {
			Rectangle2D charBounds = fontMetrics.getStringBounds(String.valueOf(strs[i]), g2d);
			int charX = i * fW + fSp + 3;
			int fY = height - ((height - (int) charBounds.getHeight()) >> 1); // 文字的纵坐标
			int charY = fY - (int) charBounds.getHeight(); // 边界框的顶部y坐标
			
			CharacterBoundingBox bbox = new CharacterBoundingBox(
				strs[i],
				charX,
				charY,
				(int) charBounds.getWidth(),
				(int) charBounds.getHeight()
			);
			boundingBoxes.add(bbox);
		}
		
		g2d.dispose();
		return boundingBoxes;
	}

	/**
	 * 生成验证码并获取字符边界框信息，用于机器学习标记和训练
	 * @param out 输出流
	 * @return 字符边界框信息列表，如果生成失败则返回空列表
	 */
	public List<CharacterBoundingBox> outWithBoundingBoxes(OutputStream out) {
		char[] strs = textChar();
		List<CharacterBoundingBox> boundingBoxes = new ArrayList<>();
		
		if (graphicsImageWithBoundingBoxes(strs, out, boundingBoxes)) {
			return boundingBoxes;
		}
		return new ArrayList<>();
	}

	/**
	 * 生成验证码图形并收集字符边界框信息
	 * @param strs 验证码
	 * @param out 输出流
	 * @param boundingBoxes 用于收集边界框信息的列表
	 * @return boolean
	 */
	private boolean graphicsImageWithBoundingBoxes(char[] strs, OutputStream out, List<CharacterBoundingBox> boundingBoxes) {
		BufferedImage bi = null;
		try {
			bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = (Graphics2D) bi.getGraphics();
			// 填充背景
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, width, height);
			// 抗锯齿
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			// 画干扰圆
			drawOval(2, g2d);
			// 画干扰线
			g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
			drawBesselLine(1, g2d);
			// 画字符串并收集边界框信息
			g2d.setFont(getFont());
			FontMetrics fontMetrics = g2d.getFontMetrics();
			int fW = width / strs.length; // 每一个字符所占的宽度
			int fSp = (fW - (int) fontMetrics.getStringBounds("W", g2d).getWidth()) / 2; // 字符的左右边距
			for (int i = 0; i < strs.length; i++) {
				g2d.setColor(color());
				Rectangle2D charBounds = fontMetrics.getStringBounds(String.valueOf(strs[i]), g2d);
				int charX = i * fW + fSp + 3;
				int fY = height - ((height - (int) charBounds.getHeight()) >> 1); // 文字的纵坐标
				int charY = fY - (int) charBounds.getHeight(); // 边界框的顶部y坐标
				
				g2d.drawString(String.valueOf(strs[i]), charX, fY - 3);
				
				// 收集边界框信息
				CharacterBoundingBox bbox = new CharacterBoundingBox(
					strs[i],
					charX,
					charY,
					(int) charBounds.getWidth(),
					(int) charBounds.getHeight()
				);
				boundingBoxes.add(bbox);
			}
			g2d.dispose();
			ImageIO.write(bi, "png", out);
			out.flush();
			return true;
		}
		catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		finally {
			try {
				out.close();
			}
			catch (IOException e) {
				log.error(e.getMessage(), e);
			}

			if (bi != null) {
				bi.getGraphics().dispose();
			}
		}
		return false;
	}

}
