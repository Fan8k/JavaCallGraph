package cn.fan.service;

import java.util.ArrayList;
import java.util.List;

import cn.fan.model.CG_Info;

public class CGservice {

	public static List<CG_Info> convertStrToCGInfos(String jarName, String invokeStr) {
		List<CG_Info> cg_Infos = new ArrayList<CG_Info>();
		String[] invokeInfos = invokeStr.split("\n");
		for (String invokeInfo : invokeInfos) {
			if (invokeInfo.startsWith("M")) {
				String[] CG_InfoStr = invokeInfo.split("\\s+");
				CG_Info cg_Info = new CG_Info();
				cg_Info.setSource_method(CG_InfoStr[0].substring(2));
				cg_Info.setTarget_method(CG_InfoStr[1].substring(3));
				cg_Info.setSource_method_dot_name(CG_InfoStr[0].substring(2).replace(':', '.'));
				cg_Info.setTarget_method_dot_name(CG_InfoStr[1].substring(3).replace(':', '.'));
				cg_Info.setLine_number(CG_InfoStr[2].trim());
				cg_Info.setProject_name(jarName);
				cg_Infos.add(cg_Info);
			}
		}
		return cg_Infos;
	}
}
