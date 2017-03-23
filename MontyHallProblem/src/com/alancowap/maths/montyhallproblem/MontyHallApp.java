/**
 * Program to simulate the Monty Hall Problem
 * Test random door selection is ok, if so proceed and pick some doors
 * Gather stats on wins vs losses for staying with door vs changing door
 * Allow for variable number of doors >= 3
 * 
 * 
 * @author Alan Cowap
 * @date 20170317 (17-March-2017)
 * @version 1.0
 * @see <a href-"http://www.alancowap.com/2017/03/22/monty-hall-problem/">Monty Hall Problem</a>
 * @see <a href-"http://www.alancowap.com/2017/03/23/monty-hall-solution-advanced/">Monty Hall Solution</a>
 * 
 */
package com.alancowap.maths.montyhallproblem;

import java.util.Random;

public class MontyHallApp {

	private static final int UPPER_BOUND = 999999;		// Random numbers chosen are below this limit 
	private static final int TEST_COUNT = 10_000_000;	//Number of times to play the game
	private static final int NUM_DOORS = 6;				// Usually 3, minimum is 3.

	/**
	 * @param args command line parameters (not used)
	 */
	public static void main(String[] args) {
		System.out.println("Welcome to the Monty Hall Problem App");

		MontyHallApp app = new MontyHallApp();
		Random carDoorNumberGenerator = null;
		Random playerDoorNumberGenerator = null;
		
		// Get some Random objects that perform within acceptable parameters
		do {
			carDoorNumberGenerator = null;
			playerDoorNumberGenerator = null;
			carDoorNumberGenerator = new Random();
			playerDoorNumberGenerator = new Random();
		
		} while (!app.isRandomAcceptable(carDoorNumberGenerator, TEST_COUNT, 1.0F) &&
				!app.isRandomAcceptable(playerDoorNumberGenerator, TEST_COUNT, 1.0F));
		
		// Now we have acceptable Randoms, let's play the Door game with different amounts of doors!
		System.out.println("\nWe have two acceptable Randoms, let's proceed...");
		for (int numDoors=3; numDoors < 10; ++numDoors) {
//			app.playDoorGame(carDoorNumberGenerator, playerDoorNumberGenerator, TEST_COUNT, NUM_DOORS);
			app.playDoorGame(carDoorNumberGenerator, playerDoorNumberGenerator, TEST_COUNT, numDoors);
		}

	}
	
	/**
	 * Play the Monty Hall Problem
	 * 
	 * @param carDoorNumber a {@link java.util.Random} object reference generating number of door where the car is
	 * @param playerDoorNumber a {@link java.util.Random} object reference generating the players Door number choice
	 * @param testCount number of times we want to play the game
	 * @param numDoors number of Doors in the game (usually 3)
	 */
	private void playDoorGame(Random carDoorNumber, Random playerDoorNumber, int testCount, int numDoors) {
		Door[] doors = new Door[numDoors];
		for (int i=0; i < doors.length; ++i)
			doors[i] = new Door();
		
		int countNoChangeWin = 0, countNoChangeLoss = 0, countChangeWin = 0, countChangeLoss = 0;

		for(int i=0; i < testCount; ++i) {
			//Choose which door the Car is behind
			int carDoorIndex = chooseDoor(doors, carDoorNumber);
			int playerDoorIndex = chooseDoor(doors, playerDoorNumber);
			
			// If winner then increment "don't change win counter" & "do change loss counter"
			if (carDoorIndex == playerDoorIndex) {
				++countNoChangeWin; // if you're winning, don't change to stay a winner.
				++countChangeLoss; 	// if you're winning, do change and you're not a winner.
				continue;
			}
			++countNoChangeLoss;//we're losing, if we don't change we lose

			//"Open" a door (not the current Player door or the Car door)
			int openDoorIndex = -1;
			Random openDoorNumberGenerator = new Random(); 
			do {
				openDoorIndex = this.chooseDoor(doors, openDoorNumberGenerator);
			}while ( (openDoorIndex == carDoorIndex) || (openDoorIndex == playerDoorIndex) );

			// Player picks another door from the remaining doors (not the current Player door, or Open door)
			int playerSecondChoiceDoorIndex = -1;
			Random playerSecondDoorNumberGenerator = new Random();
			do {
				playerSecondChoiceDoorIndex = this.chooseDoor(doors, playerSecondDoorNumberGenerator);
			}while ( (playerSecondChoiceDoorIndex == openDoorIndex) || 
					(playerSecondChoiceDoorIndex == playerDoorIndex) );
			
//			System.out.printf("\nCar Door %d, 1st Pick %d, 2nd Pick %d", 
//					carDoorIndex, playerDoorIndex, playerSecondChoiceDoorIndex );
			
			// If changing give us a winner
			if (playerSecondChoiceDoorIndex == carDoorIndex) {
				++countChangeWin;	//we were losing, but changed and we won
			}else {
				++countChangeLoss; // we were losing, and changed to another loser				
			}
		}
		
		//Output counters for Unchanged win, Change win, etc.
		System.out.printf("\nDoors in Game: %d", numDoors);
//		System.out.printf("\nUnchanged Wins: %d Lost:%d, Changed Wins: %d Lost:%d",
//				countNoChangeWin, countNoChangeLoss, countChangeWin, countChangeLoss);
		System.out.printf("\nUnchanged Wins: %d, Changed Wins: %d",
				countNoChangeWin, countChangeWin);
		float noChangeWinPercent = 100.0F * countNoChangeWin / TEST_COUNT;
		float noChangeLossPercent = 100.0F * countNoChangeLoss / TEST_COUNT;
		float changeWinPercent = 100.0F * countChangeWin / TEST_COUNT;
		float changeLosePercent = 100.0F * countChangeLoss / TEST_COUNT;
//		System.out.printf("\nUnchanged Wins: %2.2f%% Lost: %2.2f%%, Changed Wins: %2.2f%% Lost %2.2f%%",
//				noChangeWinPercent, noChangeLossPercent, changeWinPercent, changeLosePercent);
		System.out.printf("\nUnchanged Wins: %2.2f%%, Changed Wins: %2.2f%%",
				noChangeWinPercent, changeWinPercent);
		
	}
	
	/**
	 * This class is provided for future updates to allow state be stored with the Doors.
	 * It will also allow for Collections to be used and leverage .equals(), contains(), add() etc.
	 * 
	 */
	private class Door{
		public int numTimesChosenForCar = 0;
		public int numTimesChosenByPlayer = 0;
		public boolean hasCar = false;
	}

	
	
	/**
	 * Choose a Door index from the Door array passed in using the random number generator passed in
	 * 
	 * @param doors array of Door objects to choose from
	 * @param random random number generator to use
	 * @return int the index of the chosen Door from the array
	 */
	private int chooseDoor(Door[] doors, Random random) {
		int num = random.nextInt(UPPER_BOUND);
		
		float chosenDoorRange = Math.floorDiv(UPPER_BOUND, doors.length) +1 ; //TODO edge cases
		int chosenDoorIndex =  Math.floorDiv(num, (int) chosenDoorRange);
		//System.out.println("\n Chosen Door index " + chosenDoorIndex);
		
		return chosenDoorIndex;
	}

	
	
	/**
	 * 
	 * Tests whether the Random object is operating within acceptable parameters
	 * i.e. does it provide a reasonably even spread within the defined percent difference
	 * 
	 * @param r a {@link java.util.Random object reference}
	 * @param testCount number of times we want to play the game
	 * @param percentDifference spread less than or equal to this percent is deemed acceptable
	 * @return true if the spread is within the percentDifference, false otherwise
	 */
	private boolean isRandomAcceptable(Random r, int testCount, float percentDifference) {
		float total = 0;
		int minNum= Integer.MAX_VALUE;
		int maxNum = Integer.MIN_VALUE;
		int countDoor1 = 0, countDoor2 = 0, countDoor3=0;
		
		for(int i=0; i < testCount; ++i) {
			int num = r.nextInt(UPPER_BOUND);
			total += num;
			if (num < minNum) minNum = num;
			else if (num > maxNum) maxNum = num; //both these are true only on first iteration
			if (num < (UPPER_BOUND / 3)) ++countDoor1;
			else if (num < (UPPER_BOUND *2 / 3)) ++countDoor2;
			else ++countDoor3;			
//			System.out.println(num);
		}
		
		float mean = total / testCount;
		System.out.printf("\nActual Total %2.2f , Mean %2.2f", total, mean);
		
		float statMean = UPPER_BOUND / 2.0F; 
		float statTotal = testCount * UPPER_BOUND / 2.0F;
		System.out.printf("\nStatistical Total %2.2f, Mean %2.2f", statTotal, statMean);
		
		float totalPercentageDifference = 100.0F - (100.0F * total / statTotal);
		float meanPercentageDifference = 100.0F - (100.0F * mean /statMean);
		System.out.printf("\nPercent Difference: Total %2.2f, Mean %2.2f ",
				totalPercentageDifference, meanPercentageDifference);
		
		System.out.printf("\nMin %d, Max %d", minNum, maxNum);
		System.out.printf("\nDoor1 %d, Door2 %d, Door3 %d", countDoor1, countDoor2, countDoor3);
		int totalDoorsChosen = countDoor1 + countDoor2 + countDoor3;  
		System.out.printf("\nDoors chosen %d, minus total doors (1..3)chosen %d, diff (should be 0) %d",
				testCount, totalDoorsChosen, testCount - totalDoorsChosen);
		
		float lowerLimit = (testCount / 3) - (testCount * percentDifference / 100);
		float upperLimit = (testCount / 3) + (testCount * percentDifference / 100);
		System.out.printf("\nlowerLimit %f, upperLimit %f", lowerLimit, upperLimit);
		boolean door1Acceptable = (countDoor1 <= upperLimit) && (countDoor1 >= lowerLimit);
		boolean door2Acceptable = (countDoor2 <= upperLimit) && (countDoor2 >= lowerLimit);		
		boolean door3Acceptable = (countDoor3 <= upperLimit) && (countDoor3 >= lowerLimit);
		boolean isAcceptable = door1Acceptable && door2Acceptable && door3Acceptable; 
		if (!isAcceptable) System.err.println("Rejected Random, trying again.");

		return (isAcceptable);		
	}
	
}
