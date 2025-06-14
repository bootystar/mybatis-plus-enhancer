package io.github.bootystar.mybatisplus.enhancer.core.base;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.bootystar.mybatisplus.enhancer.util.CastHelper;
import io.github.bootystar.mybatisplus.enhancer.util.ExcelAdapter;
import io.github.bootystar.mybatisplus.enhancer.util.ReflectHelper;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bootystar
 */
@SuppressWarnings("unused")
public interface EnhancedExcel {

    default void excelTemplate(OutputStream os, Class<?> clazz) {
        ExcelAdapter.write(os, clazz, Collections.emptyList());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default int excelImport(InputStream is, Class<?> clazz) {
        List<?> dataList = ExcelAdapter.read(is, clazz);
        if (dataList == null || dataList.isEmpty()) return 0;
        IService iService = CastHelper.cast(this, IService.class);
        List<?> entityList = dataList.stream()
                .map(e -> ReflectHelper.toTarget(e, iService.getEntityClass()))
                .collect(Collectors.toList());
        iService.saveBatch(entityList);
        return entityList.size();
    }

    default void excelExport(Object s, OutputStream os, Class<?> clazz, String... includeFields) {
        excelExport(s, os, clazz, 1L, -1L, includeFields);
    }

    @SuppressWarnings("rawtypes")
    default void excelExport(Object s, OutputStream os, Class<?> clazz, Long current, Long size, String... includeFields) {
        EnhancedQuery enhancedQuery = CastHelper.cast(this, EnhancedQuery.class);
        List<?> voList = enhancedQuery.voPage(s, current, size).getRecords();
        ExcelAdapter.write(os, clazz, voList, includeFields);
    }

}
