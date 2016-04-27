package com.dp.simplerest.control;

import org.springframework.stereotype.Component;

import com.dp.simplerest.annotation.Rest;

@Component
@Rest
public class TestController {

	@Rest(path = "/test")
	public String index() {
		return "Hello World!";
	}

	@Rest(path = "/test/query")
	public Resolution query(long id) {
		Resolution resolution = new Resolution();
		resolution.setHeight(800);
		resolution.setWidth(600);
		return resolution;
	}

	@Rest(path = "/test/insert")
	public void insert(long id, String value) {
		System.out.println("Invoke insert id=" + id + " value=" + value);
	}

	public void notbinded() {
	}

	class Resolution {
		private int height;
		private int width;

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

	}
}
