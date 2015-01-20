package com.zach.mapgen;

import java.util.Random;

public class MapGenerator {

	private static final int TopLeft = 0;
	private static final int TopRight = 1;
	private static final int BottomRight = 2;
	private static final int BottomLeft = 3;
	
	private static final int Top = 0;
	private static final int Right = 1;
	private static final int Bottom = 2;
	private static final int Left = 3;
	
	
	public static final float HEIGHTSCALINGFACTOR = 100;
	
	private float[][] map;
//	private IntVector2[] cornerPositions;
	
	public  float[][] genDiamondSquareHeightMap(float[][] map, long seed){
		this.map = map;
		//IntVector2[] cornerPositions = new IntVector2[4];
		if(map == null){
			System.out.println("Map array is not initialized");
			return map;
		}else if(!isPowerOfTwo(map[0].length-1) || !isPowerOfTwo(map.length-1)){
			System.out.println("Map must have side length 1+2^x");
			return map;
		}
		Random ran = new Random(seed);
		
		initCorners(ran);
		IntVector2 initialCorners[] = {new IntVector2(0,map.length), 
										new IntVector2(map[0].length, map.length),
										new IntVector2(map[0].length, 0),
										new IntVector2()};
		diamondSquare(initialCorners);
		
		return map;
	}
	

	private void diamondSquare(IntVector2[] corners){
		int xDiff = corners[BottomRight].x - corners[BottomLeft].x;
		int yDiff = corners[TopLeft].y - corners[BottomLeft].y;

		if(xDiff == 0 || yDiff == 0){
			return;
		}
		
		//midpoint
		IntVector2 midPoint = new IntVector2(corners[BottomLeft].y+yDiff, corners[BottomLeft].x+xDiff);
		map[midPoint.y][midPoint.x] = averageWeight(corners); 
		
		IntVector2 leftMidPos = new IntVector2();
		IntVector2 topMidPos = new IntVector2();
		IntVector2 rightMidPos = new IntVector2();
		IntVector2 bottomMidPos = new IntVector2();
		
		//leftMid
		leftMidPos.y = corners[BottomLeft].y+yDiff;
		leftMidPos.x = corners[BottomLeft].x;
		
		map[leftMidPos.y][leftMidPos.x] = averageWeight(getDiamondCorners(leftMidPos, xDiff));
		
		//topMId
		topMidPos.y = corners[TopLeft].y;
		topMidPos.x = corners[TopLeft].x+xDiff;
		map[topMidPos.y][topMidPos.x] = averageWeight(getDiamondCorners(topMidPos, xDiff));
		//rightMid
		rightMidPos.y = corners[BottomRight].y+yDiff;
		rightMidPos.x = corners[BottomRight].x;
		map[rightMidPos.y][rightMidPos.x] = averageWeight(getDiamondCorners(rightMidPos, xDiff));
		
		//bottomMid
		bottomMidPos.y = corners[BottomLeft].y;
		bottomMidPos.x = corners[BottomLeft].x+xDiff;
		map[bottomMidPos.y][bottomMidPos.x] = averageWeight(getDiamondCorners(bottomMidPos, xDiff));
		
		IntVector2 newCorners[] = new IntVector2[4];
		newCorners[TopLeft] =corners[TopLeft];
		newCorners[TopRight] =topMidPos; 
		newCorners[BottomRight] =midPoint;
		newCorners[BottomLeft] =leftMidPos;
		diamondSquare(newCorners);
		newCorners[TopLeft] =topMidPos;
		newCorners[TopRight] =corners[TopRight]; 
		newCorners[BottomRight] =rightMidPos;
		newCorners[BottomLeft] =midPoint;
		diamondSquare(newCorners);
		newCorners[TopLeft] =midPoint;
		newCorners[TopRight] =rightMidPos; 
		newCorners[BottomRight] =corners[BottomRight];
		newCorners[BottomLeft] =bottomMidPos;
		diamondSquare(newCorners);
		newCorners[TopLeft] =leftMidPos;
		newCorners[TopRight] =midPoint; 
		newCorners[BottomRight] =bottomMidPos;
		newCorners[BottomLeft] =corners[BottomLeft];
		diamondSquare(newCorners);
	}
	
	private float averageWeight(IntVector2[] cornerPositions){
		float avg = 0;
		int i = 0;
		float weight = -1;
		for(IntVector2 iV : cornerPositions){
			weight = getWeightAt(iV);
			if(weight != -1){
				i++;
				avg += weight;
			}
		}
		
		return avg/i;
	}
	
	private IntVector2[] getDiamondCorners(IntVector2 mid, int reach){
		IntVector2 top = new IntVector2(mid.x,mid.y+reach);
		IntVector2 right = new IntVector2(mid.x+reach,mid.y);
		IntVector2 bottom = new IntVector2(mid.x,mid.y-reach);
		IntVector2 left = new IntVector2(mid.x-reach,mid.y);
		IntVector2 diamondCorners[] = {top,right,bottom,left};
		return diamondCorners;
	}
	
	private float getWeightAt(IntVector2 pos){
		return getWeightAt(pos.x, pos.y);
	}
	
	private float getWeightAt(int x, int y){
		if(x < 0 || x > map[0].length || y < 0 || y > map.length){
			return -1;
		}
		return map[y][x];
	}
	
	private void initCorners(Random ran){
		map[0][0]                      = ran.nextFloat() * HEIGHTSCALINGFACTOR;
		map[map.length][0]             = ran.nextFloat() * HEIGHTSCALINGFACTOR;
		map[0][map[0].length] 		   = ran.nextFloat() * HEIGHTSCALINGFACTOR;
		map[map.length][map[0].length] = ran.nextFloat() * HEIGHTSCALINGFACTOR;
	}
	
	
	private static boolean isPowerOfTwo(int number){
		return (number & (number - 1)) == 0;
	}
	
	
	private class IntVector2{
		
		public int x;
		public int y;
		
		public IntVector2(){
			this(0,0);
		}
		
		public IntVector2(int x, int y){
			this.x = x;
			this.y = y;
		}
	}
}
