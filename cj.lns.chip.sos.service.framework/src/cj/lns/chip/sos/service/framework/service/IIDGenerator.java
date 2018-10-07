package cj.lns.chip.sos.service.framework.service;

import java.math.BigInteger;

import cj.lns.common.sos.service.moduleable.CjTransaction;

public interface IIDGenerator {

	/**
	 *  规则：xxxddddeeee,前三位是区别代码，有：，中四位是用户指定码，后n位系统生成，n位生成根据量产生
	 * <pre>
	 *
	 * </pre>
	 * @return
	 */
	BigInteger genUserId(int kindCode, int userSetCode);

	/**
	 * 规则：xxxddddeeee,前三位是区别代码，有：280,910等等，中四位是用户指定码，后n位系统生成，n位生成根据量产生
	 * <pre>
	 *	
	 * </pre>
	 * @return
	 */
	BigInteger genSwsId(int kindCode, int userSetCode);

}