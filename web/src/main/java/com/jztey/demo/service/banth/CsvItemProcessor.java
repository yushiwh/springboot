package com.jztey.demo.service.banth;

import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;

import com.jztey.demo.entity.BanthPerson;

/**
 * 数据处理及校验
 * 
 * @author yushi
 *
 */
public class CsvItemProcessor extends ValidatingItemProcessor<BanthPerson> {

	@Override
	public BanthPerson process(BanthPerson banthPerson) throws ValidationException {
		super.process(banthPerson);// 需要调用super.process才会调用自定义的校验器
		if (banthPerson.getNation().equals("汉族")) {// 对数据进行简单的处理，为汉族的时候为01，不是为02
			banthPerson.setNation("01");
		} else {
			banthPerson.setNation("02");
		}
		return banthPerson;
	}

}
