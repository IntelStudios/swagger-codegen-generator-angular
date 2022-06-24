package com.intelstudios;

import io.swagger.codegen.v3.*;
import io.swagger.codegen.v3.generators.typescript.AbstractTypeScriptClientCodegen;
import io.swagger.codegen.v3.generators.typescript.TypeScriptAngularClientCodegen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static io.swagger.codegen.v3.CodegenConstants.IS_ENUM_EXT_NAME;
import static io.swagger.codegen.v3.generators.handlebars.ExtensionHelper.getBooleanValue;

public class AngularEnumsGenerator extends TypeScriptAngularClientCodegen {

  private static Logger LOGGER = LoggerFactory.getLogger(AbstractTypeScriptClientCodegen.class);

  public String getName() {
    return "typescript-angular-enums";
  }

  public String getHelp() {
    return "Generates a TypeScript Angular (2.x or 4.x) client library.";
  }

  @Override
  public void processModelEnums(Map<String, Object> objs) {
    List<Object> models = (List<Object>) objs.get("models");
    for (Object _mo : models) {
      Map<String, Object> mo = (Map<String, Object>) _mo;
      CodegenModel cm = (CodegenModel) mo.get("model");
      // for enum model
      boolean isEnum = getBooleanValue(cm, IS_ENUM_EXT_NAME);
      if (Boolean.TRUE.equals(isEnum) && cm.allowableValues != null) {

        // read enum names from vendor extension data
        List<String> enumVarNames = (List<String>)cm.getVendorExtensions().get("x-enumNames");

        Map<String, Object> allowableValues = cm.allowableValues;
        List<Object> values = (List<Object>) allowableValues.get("values");
        List<Map<String, String>> enumVars = new ArrayList<Map<String, String>>();
        String commonPrefix = findCommonPrefixOfVars(values);
        int truncateIdx = commonPrefix.length();
        int index = 0;
        for (Object value : values) {
          Map<String, String> enumVar = new HashMap<String, String>();
          String enumName = findEnumName(truncateIdx, value);
          String enumVarName = enumVarNames != null ? enumVarNames.get(index) : toEnumVarName(enumName, cm.dataType);
          enumVar.put("name", enumVarName);
          if (value == null) {
            enumVar.put("value", toEnumValue(null, cm.dataType));
          } else {
            enumVar.put("value", toEnumValue(value.toString(), cm.dataType));
          }
          enumVars.add(enumVar);
          index++;
        }
        cm.allowableValues.put("enumVars", enumVars);
      }
      updateCodegenModelEnumVars(cm);
    }
  }

  private String findEnumName(int truncateIdx, Object value) {
    if (value == null) {
      return "null";
    }
    String enumName;
    if (truncateIdx == 0) {
      enumName = value.toString();
    } else {
      enumName = value.toString().substring(truncateIdx);
      if ("".equals(enumName)) {
        enumName = value.toString();
      }
    }
    return enumName;
  }
}
