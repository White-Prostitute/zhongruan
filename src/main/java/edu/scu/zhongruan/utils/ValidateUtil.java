package edu.scu.zhongruan.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

//校验工具类
public class ValidateUtil {

    public static boolean isValidIdentificationNumber(String id) {
        if(Objects.isNull(id)){
            return false;
        }
        // 校验长度
        if (id.length() != 18) {
            return false;
        }
        // 校验格式
        if (!id.matches("\\d{17}[\\dX]")) {
            return false;
        }
        // 校验地区
        String[] areas = new String[] {
                "11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34", "35", "36", "37", "41", "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63", "64", "65", "71", "81", "82"
        };
        List<String> areaList = Arrays.asList(areas);
        if (!areaList.contains(id.substring(0, 2))) {
            return false;
        }
        // 校验出生日期
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat.setLenient(false);
        try {
            Date birthDate = dateFormat.parse(id.substring(6, 14));
            if (birthDate.after(new Date())) {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
        // 校验校验码
        int[] factors = new int[] {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        String[] codes = new String[] {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
        int sum = 0;
        for (int i = 0; i < factors.length; i++) {
            sum += factors[i] * Integer.parseInt(id.substring(i, i + 1));
        }
        int index = sum % 11;
        return codes[index].equals(id.substring(17));
    }


}
