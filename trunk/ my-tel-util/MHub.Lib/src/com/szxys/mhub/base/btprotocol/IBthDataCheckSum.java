package com.szxys.mhub.base.btprotocol;

interface IBthDataCheckSum
{
	/**
	 * aStartPos and aEndPos use [left,right) convention
	 * @param aBuffer part of this buffer will be sum checked
	 * @param aStartPos start index, including
	 * @param aEndPos end index, excluding
	 * @return checked value
	 */
	byte SumCheck(byte[] aBuffer, int aStartPos,int aEndPos);
	
	/**
	 * crc check
	 * @param aBuffer part of this buffer will be sum checked
	 * @param aCheckedLength the length to be checked in the buffer, the checked index start with 0
	 * @return checked value
	 */
//	byte crc(byte[] aBuffer,int aCheckedLength);
//	
//	/**
//	 * aStartPos and aEndPos use [left,right) convention
//	 * @param aBuffer part of this buffer will be sum checked
//	 * @param aStartPos start index, including
//	 * @param aEndPos end index, excluding
//	 * @return checked value
//	 */
	byte crc(byte[] aBuffer, int aStartPos,int aEndPos);
}
