package com.hz.aicodelessappbuilder.core.saver;

import com.hz.aicodelessappbuilder.ai.model.MultiFileCodeResult;
import com.hz.aicodelessappbuilder.model.enums.CodeGenTypeEnum;

public class MultiFileCodeFileSaverTemplate extends CodeFileSaverTemplate<MultiFileCodeResult> {


    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    protected void saveFiles(MultiFileCodeResult result, String baseDirPath) {
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
        writeToFile(baseDirPath, "script.js", result.getJsCode());
        writeToFile(baseDirPath, "style.css", result.getCssCode());
    }
}
