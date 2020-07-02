# 文件模板
为了减少样板代码，可以使用 AS 提供的 File Template 和 Live Template。

## 1. Activity 文件模板
```text
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}

#end
class ${NAME} : BaseActivity<Activity${Xml_binding}Binding, ${Xml_vm}ViewModel> (
    R.layout.activity_${activity_xml}, BR.viewModel
) {
}
```

## 2. Fragment 文件模板
```text
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}

#end
#parse("File Header.java")
class ${NAME} : BaseFragment<Fragment${Xml_binding}Binding, ${Xml_vm}ViewModel> (
    R.layout.fragment_${fragment_xml}, BR.viewModel
) {
}
```

## 3. ViewModel 文件模板
```text
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME}

#end
#parse("File Header.java")
class ${NAME}(app: Application) : BaseViewModel<BaseModel>(app) {
}
```

## 4. viewModel 变量代码模板
```text
<variable
    name="viewModel"
    type="$vm$"
    />
```

或者通过 File | Import Settings 导入 templates.zip
