package com.hz.aicodelessappbuilder.core.saver;

import com.hz.aicodelessappbuilder.ai.model.HtmlCodeResult;
import com.hz.aicodelessappbuilder.model.enums.CodeGenTypeEnum;

import java.io.File;

public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {


    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
    }
}
