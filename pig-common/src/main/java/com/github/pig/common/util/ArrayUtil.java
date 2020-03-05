package com.github.pig.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 集合工具类
 * @author yuwei
 * @date 2019/1/11 11:52
 */
@Slf4j
public class ArrayUtil {
    private ArrayUtil() {
    }

    /**
     * 对比两个集合时候想等
     * @param a
     * @param b
     * @param <Q>
     * @return
     */
    public static <Q> boolean equals(List<Q> a, List<Q> b) {
        if (a.size() != b.size())
            return false;
        if (a.isEmpty())
            return false;
        if (b.isEmpty())
            return false;
        Q q;
        for (int i = 0; i < a.size(); i++) {
            q = a.get(i);
            for (int x = 0; x < b.size(); x++) {
                if (q.equals(b.get(x))) {
                    log.info("有一个元素的值是相等的");
                    break;
                } else {
                    if (x == b.size()) {
                        log.info("没有一个元素是相等的");
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
