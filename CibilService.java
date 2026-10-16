package com.loan;

import java.util.Random;
public class CibilService {
 
	public static int generateCibilScore() {

        Random random = new Random();

        return 600 + random.nextInt(301);

}
}
