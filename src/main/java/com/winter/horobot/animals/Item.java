package com.winter.horobot.animals;

public class Item {

	private final String file;
	private final String name;
	private final int type;

	public Item(String path, String name, int type) {
		this.file = path;
		this.name = name;
		this.type = type;
	}

	public String getFile() {
		return file;
	}

	public String getName() {
		return name;
	}

	public int getType() { return type; }

	@Override
	public boolean equals(Object o) {
		return o == this || (o instanceof Item && ((Item) o).getName().toUpperCase().equals(this.getName().toUpperCase()));
	}
}
