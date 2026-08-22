package com.system.lld.locker.model;

public enum testE {

	SMALL("small", 10), MEDIUM("medium", 15), LARGE("large", 20);

	private String size;
	private Integer fare;

	testE(String size, Integer fare) {
		this.size = size;
		this.fare = fare;
	}

	public static void main() {
		
		String packageSize = "medium";
		
		testE box = null;
		
		for(testE t1 : testE.values()) {
			if(t1.size.equalsIgnoreCase(packageSize)) {
				box= t1;
				break;
			}
		}
		//System.out.println(box.size +"  "+ box.fare);
		
		int i = box.ordinal();
		testE[] values = testE.values();
		
		for(int j=i ; j<values.length; j++) {
			testE newBox = values[j];
			System.out.println(newBox.size +"  "+ newBox.fare);
		}

	}

}
