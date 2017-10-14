# ScrollRulerView
Android滑动卷尺控件<br>

## 效果图
gif(有点被拉伸了):<br>
![image1](https://github.com/CCY0122/ScrollRulerView/blob/master/device-2017-10-14-203736.gif)
<br>screenshot:<br>
![image2](https://github.com/CCY0122/ScrollRulerView/blob/master/device-2017-10-14-205054.png)

## 使用方法

 直接布局里引用即可:<br>
```
    <ccy.scrollrulerview.ScrollRulerView
        android:layout_width="match_parent"
        android:layout_height="150dp"/>
```
可用的属性：<br>
        
| xml属性 | 对应方法 | 作用 | 默认值 |
|--------|----------|------|--------|
| primary_color | setPrimaryColor | 当前刻度文字颜色 | 0xFF3CB371 |
| primary_text_size | setPrimaryTextSize | 当前刻度文字大小 | 30sp |
| text_color | setTextColor | 刻度文字颜色 | 0xFF000000 |
| text_size | setTextSize |刻度文字大小 | 15sp | 
| line_color | setLineColor | 刻度线颜色 | 0xFF888888 |
| rulerBackgroundColor | setRulerBackground | 刻度版背景颜色 | 透明 |
| unit | setUnit |单位 | kg |
| start_num | setStartNum | 起点值 | 0 |
| end_num | setEndNum | 终点值 | 100 |
| min_gap | setMinGap | 最小刻度间隔 | 10dp |
| /  |  setCurrentValue  | 设置当前刻度值 | 起点值 |
