package com.guanri.android.jpos.pos.data.Fields;

import com.guanri.android.jpos.pos.data.Common;
import com.guanri.android.jpos.pos.data.Fields.TField.TDataType;
import com.guanri.android.jpos.pos.data.Fields.TField.TLengthType;

public class TFieldList {
	public enum TResult_LoadFromBytes {
		rfll_NoError /* 无错误 */, rfll_LessBytes /* 数据流长度过短 */, rfll_InvalidField /* 无效的字段 */, rfll_InsufficientField /* 缺少字段 */, rfll_RedundantField
		/* 多余的字段 */
	};

	public enum TResult_SaveToBytes {
		rfls_NoError, rfls_EmptyField /* 空字段 */, rfls_InvalidField
		/* 无效的字段 */
	};

	protected int FFristFieldIndex = 0;
	public TField[] FFields = null;

	public TField GetField(int Index) {
		Index -= FFristFieldIndex;
		if ((0 <= Index) & (Index <= Common.High(FFields)))
			return FFields[Index];
		else
			return null;

	}

	public void AddField(int AIndex, TDataType ADataType,
			TLengthType ALengthType, int AMaxLength, String AName) {
		TField Field;
		switch (ADataType) {
		case dt_BIN:
			Field = new TBINField();
			break;
		case dt_BCD:
			Field = new TBCDField();
			break;
		case dt_ASC:
			Field = new TASCField();
			break;
		default:
			return;
		}
		
		Field.FIndex = AIndex;
		Field.FLengthType = ALengthType;
		Field.FMaxLength = AMaxLength;
		Field.FName = AName;
		
		AddField(Field);
	}
	public void AddField(TField Field) {
		if (Field == null) return;
		int k, i;
		
		
		  k = Field.FIndex - FFristFieldIndex;

		  if (k < 0 ) {
	
		    k = -k;
		    FFields = Common.SetLength(FFields, Common.High(FFields) + 1 + k);
		    for (i = Common.High(FFields); i >= k; i--) 
		    	FFields[i] = FFields[i - k];
	
		    for (i = 1; i < k; i++) 
		      FFields[i] = null;

		    FFristFieldIndex -= k;
		    k = 0;
		  }
		  else if (k > Common.High(FFields))
		  	FFields = Common.SetLength(FFields, k + 1);
		  else{
		    if (FFields[k] != null) return;

		  }
		  FFields[k] = Field;
	}
	
	public TResult_LoadFromBytes LoadFormBytes() {
		TField.TResult_LoadFromBytes R;
		for (int i = 0; i < Common.Length(FFields); i++) {
			if (FFields[i] != null) {
				R = FFields[i].LoadFromBytes();
				if (R != TField.TResult_LoadFromBytes.lfr_NoError) {
					if (R == TField.TResult_LoadFromBytes.lfr_LessBytes) 
						return TResult_LoadFromBytes.rfll_LessBytes;
					else
						return TResult_LoadFromBytes.rfll_InvalidField;
				}
			}	
		}
		return TResult_LoadFromBytes.rfll_NoError;
	}
	
	public TResult_SaveToBytes SaveToBytes() {
		TField.TResult_SaveToBytes R;
		for (int i = 0; i < Common.Length(FFields); i++) {
			if (FFields[i] != null) {
				R = FFields[i].SaveToBytes();
				if (R != TField.TResult_SaveToBytes.sfr_NoError) {
					if (R == TField.TResult_SaveToBytes.sfr_EmptyField) 
						return TResult_SaveToBytes.rfls_EmptyField;
					else
						return TResult_SaveToBytes.rfls_InvalidField;
				}
			}	
		}
		return TResult_SaveToBytes.rfls_NoError;
	}

}
