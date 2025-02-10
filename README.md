# Image-Processing![image-processing](https://github.com/user-attachments/assets/53007f87-cb51-4c8b-b905-61479c74d4d8)

## I. 專題簡介

使用 Java 實作影像處理程式，具備將原圖以灰階、負片、Gamma校正（Gamma correction）、對比拉伸（Contrast stretch）、椒鹽雜訊（Salt-and-pepper noise）、 3×3 中值濾波器（Median filter）、拉普拉斯運算子（Laplace operator, Laplacian）、3×3 平均值濾波器（Average filter）和 Otsu 二值化（Otsu's method）等等常見影像處理方法。

## II. 所需函式庫：

* `java.awt.image.BufferedImage`: 用來存取、儲存和處理圖片像素。
* `java.io.File`: 操作圖片檔案。
* `java.io.IOException`: 當發生輸入輸出例外（例如：讀寫檔案失敗）時會拋出 I/O 例外。
* `javax.imageio.ImageIO`: 提供讀取與寫入圖片檔案的方法。
* `java.util.Arrays`: 提供操作陣列的方法（例如：排序）。
* `java.util.Random`: 提供生成隨機數的方法（模擬椒鹽雜訊時使用）。

## III. 程式功能：

* `GrayScale()`: 將圖片轉換為灰階。
* `Negative()`: 產生圖片的負片效果。
* `Contrast()`: 使用不同的伽瑪(Gamma)值來調整對比度。
* `SaltnPepper()`: 添加椒鹽雜訊（Salt-and-pepper noise）。
* `Median()`: 使用 3×3 的中值濾波器（Median filter）去除椒鹽雜訊。
* `Laplacian()`: 使用拉普拉斯運算子（Laplace operator, Laplacian）檢測圖片邊緣。
* `Max()`: 使用 3×3 的最大值濾波器（Max filter）強化亮點。
* `Otsu()`: 使用 Otsu（大津）方法將圖片進行二值化（Binarization）。

## III. 實作方法說明：

### i. 圖片檔案 I/O

#### 1. 如何讀取圖片檔案？

程式使用 `ImageIO.read(File file)` 來載入圖片，並將影像存入 `BufferedImage` 物件中。

```=java
File input_f = new File("lenna-orgi.bmp");    // 指定圖片檔案
BufferedImage img_orgi = ImageIO.read(input_f);    // 讀取影像
int h = img_orgi.getHeight();   // 取得影像高度
int w = img_orgi.getWidth();    // 取得影像寬度
```

#### 2. 如何讀取圖片像素？

```=java
int[] pixel = new int[h * w];    // 建立存放像素的陣列
img_orgi.getRGB(0, 0, w, h, pixel, 0, 512);    // 讀取整張圖片的像素
```

* `(0, 0, w, h)`：從 (0,0) 讀取整個 w × h 區域。
* `pixel`：將所有像素存入 pixel 陣列。
* `0`：從陣列的索引 0 開始存入數據。
* `512`：這個參數設定橫向掃描的間距，程式內部固定為 512。

#### 3. 如何處理每個像素的 RGB 值？

由於 Java 使用 32-bit 整數 (ARGB 格式) 來存放每個像素，例如：`AAAAAAAA RRRRRRRR GGGGGGGG BBBBBBBB`，因此程式將影像中的每個像素區分顏色通道各存為 `int` 值：
* Alpha (A) 透明度
* Red \(R\) 紅色通道
* Green (G) 綠色通道
* Blue (B) 藍色通道

每個顏色占 8-bit（0~255），並使用位運算子 `>>` 和 `& 0xff`（位元遮罩）來各別擷取出來。

```
int c = pixel[i];    // 取得當前像素的值
int a = (c >> 24) & 0xff;    // 擷取 Alpha
int r = (c >> 16) & 0xff;    // 擷取 Red
int g = (c >> 8) & 0xff;    // 擷取 Green
int b = (c >> 0) & 0xff;    // 擷取 Blue
```

以 `(c >> 24) & 0xff` 為例，表示將 bit 數值右移 24 位，並只保留變成最低 8-bit 的 Alpha bit。

#### 4. 如何將經過影像處理完後的像素資料寫回影像？

程式在影像處理完後，會建立新的 `BufferedImage` 物件，然後用 `setRGB()` 方法將修改後的像素資料寫入，最後再輸出為對應的圖片檔案。

```
BufferedImage img_new = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);    // BufferedImage.TYPE_INT_RGB: 指定影像格式為 RGB
img_new.setRGB(0, 0, w, h, pixel, 0, 512);    // 設定新的像素資料
File out_f = new File("output.bmp");
ImageIO.write(img_new, "bmp", out_f);    // 存成 BMP 檔案
```

### ii. 影像處理方法

#### 1. **GrayScale()－灰階轉換**

> 將彩色的原始圖片轉換為灰階影像。

1. 擷取紅（R）、綠（G）和藍（B）三個通道的像素，並使用平均值方法計算灰階值：

    ```
    Pixel_{Gray scale} = (R + G + B) / 3
    ```

2. 另存為 `lenna-grayscale.bmp`。

---

#### 2. **Negative()－負片效果**

> 反轉灰階圖片的顏色以生成負片。

1. 將每個 RGB 像素值分別用 255 減去其原始值，以產生負片效果：

    ```
    R_{Negative} = 255 - R, G_{Negative} = 255 - G, B_{Negative} = 255 - B
    ```

2. 另存為 `lenna-negative.bmp`。

---

#### 3. **Contrast()－伽瑪校正**

> 透過伽瑪值調整圖片對比度。

1. 先遍歷所有像素，找出 ARGB 像素的最大值（`a_max`, `r_max`, `g_max`, `b_max`）和最小值（`a_min`, `r_min`, `g_min`, `b_min`）。

2. 進行標準化，以紅色像素為例：

    ```
    r_{Normalized} = ((r - r_min) / (r_max - r_min)) × 255
    ```

3. 根據不同的伽瑪值 (`gamma > 1`、`gamma = 1`、`gamma < 1`)，調整亮度。
    （1）gamma = 1：影像保持原樣，無亮度變化。
    （2﹚gamma > 1：拉開亮部區域，壓縮暗部區域，使整體亮度變暗。
    （3﹚gamma < 1：壓縮亮部區域，拉開暗部區域，使整體亮度變亮。
    
    ```
    R_{Gamma correction} = (r_{Normalized}) ^ gamma × 255
    ```

4. 處理並保存不同伽瑪值的結果。
    * gamma = 1 ➝ `lenna-gamma-cs.bmp`
    * gamma > 1 ➝ `lenna-gamma-abv.bmp`
    * gamma < 1 ➝ `lenna-gamma-bel.bmp`

---

#### 4. **SaltnPepper()－椒鹽雜訊**

> 添加隨機的黑白點模擬椒鹽雜訊。

1. 首先要決定能控制雜訊強度的比率 `rt`，也就是選擇影像中 `rt * 總像素數` 的像素。
2. 使用 `Random` 產生隨機數，然後對每個像素進行判斷：如果隨機產生的值大於 `0.5`（50% 機率），將該像素設定為黑色（`0`）；小於 0.5，則將該像素設定為白色（`255`）。

```=java
if(random.nextFloat() > rt) {
    // 50% Black and 50% white
    if(random.nextFloat() > 0.5f) {
        r = 255;
        g = 255;
        b = 255;
    }
    else {
        r = 0;
        g = 0;
        b = 0;
    }
}
```

3. 另存為 `lenna-saltnpepper.bmp`。

---

#### 5. **Median()－中值濾波器**

> 平滑去除椒鹽雜訊，同時盡量保留影像的邊緣紋理。

1. 每個像素 `(i, j)` 的鄰域是一個 3×3 的矩陣滑動窗口（sliding window）：

    ```
    [P_{i-1, j-1}, P_{i-1, j}, P_{i-1, j+1}]
    [P_{  i, j-1}, P_{  i, j}, P_{  i, j+1}]
    [P_{i+1, j-1}, P_{i+1, j}, P_{i+1, j+1}]
    ```

2. 將鄰域內的所有像素值儲存至一維陣列進行排序，取中位數值作為新的像素值，並更新影像對應的像素：

    ```=
    for (int i = 1; i < h - 1; i++) {
        for (int j = 1; j < w - 1; j++) {
            int[] num = new int[9];
            int k = 0;
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    num[k++] = pixel[i + x][j + y];
                }
            }
            // 排序陣列中的元素
            Arrays.sort(num);
            // 將陣列中的中位數值指定給新影像的像素值
            pixel_m[i][j] = num[4];
		}
	}
    ```

3. 另存為 `lenna-median.bmp`。

---

#### 6. **Laplacian()－拉普拉斯轉換**

> 檢測並強化（偵測）影像邊緣。

1. 定義 3×3 拉普拉斯運算子（卷積核）。

    ```
    [ 0, -1,  0]
    [-1,  4, -1]
    [ 0, -1,  0]
    ```

2. 對於每個位置 `(i, j)` 之像素，使用拉普拉斯運算子進行卷積計算並相加，再取絕對值：
    
    ```
    |P'_{i,j}| = (P_{i-1, j} + P_{i+1, j} + P_{i, j-1} + P_{i, j+1}) - 4 × P_{i, j}
    ```

    ```=java
    int[][] lpo = {{0, -1, 0}, {-1, 4, -1}, {0, -1, 0}};  // Laplacian Operator
    int[][] pixel_proc = new int[h][w];  // Array of the processed image
    for (int x = 1; x < w - 1; x++) {
        for (int y = 1; y < h - 1; y++) {
            int sum = 0;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    sum += pixel[x + i][y + j] * lpo[i + 1][j + 1];
                }
            }
            // 為避免負值，取絕對值
            pixel_proc[x][y] = Math.abs(sum);
        }
    }
    ```
    
3. 另存為 `lenna-laplacian.bmp`。

---

#### 7. **Max()－最大值濾波器**

> 使用 3×3 的最大值濾波器去除圖片中的低頻暗點雜訊（高頻亮點雜訊會被強化）。


1. 每個像素 $(i, j)$ 的鄰域同樣是一個 3×3 的矩陣：
    
    ```
    [P_{i-1, j-1}, P_{i-1, j}, P_{i-1, j+1}]
    [P_{  i, j-1}, P_{  i, j}, P_{  i, j+1}]
    [P_{i+1, j-1}, P_{i+1, j}, P_{i+1, j+1}]
    ```
    
2. 將鄰域內的所有像素值儲存至一維陣列進行排序。
3. 取最大數值作為新的像素值，並更新影像對應的像素：

    ```
    for (int i = 1; i < h - 1; i++) {
        for (int j = 1; j < w - 1; j++) {
            int[] num = new int[9];
            int k = 0;
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    num[k++] = pixel[i + x][j + y];
                }
            }
            // 排序陣列中的元素
            Arrays.sort(num);
            // 將陣列中的中位數值指定給新影像的像素值
            pixel_new[i][j] = num[num.length - 1];
        }
    }
    ```

4. 另存為 `lenna-max.bmp`。

---

#### 8. **Otsu()－Otsu 二值化**

> 根據 Otsu 方法將灰階圖片二值化。

1. 由於是從灰階影像中讀取所有像素的灰階值，因此可以直接取最低 8 bit（`c >> 0 & 0xff`）當作灰度。
2. 計算整張影像所有像素的灰階值總和，並以平均值作為門檻值（threshold）。

    ```=
    int threshold = sum / (w * h);
    ```

3. 對於每個像素：若其灰階值大於門檻值，則將該像素設定為白色（`0xffffffff`）；小於則設定為黑色（`0xff000000`）。

    ```=
    for (int i = 0; i < w * h; i++) {
        int c = (pixel[i] >> 0) & 0xff;
        if (c > threshold) {
            pixel[i] = 0xffffffff;  // To white
        } else {
            pixel[i] = 0xff000000;  // To black
        }
    }
    ```
    
4. 另存為 `lenna-otsu.bmp`。

---

## IV. 安裝與使用

* 系統需求
1. Java 8 或以上版本。
2. 確保要處理的影像檔名為 `lenna-orgi.bmp` 並與 `ImageProcessing.java` 位於同一資料夾。

* 編譯程式：

```
javac ImageProcessing.java
```

* 執行程式：

```
java ImageProcessing
```

* 輸出結果：
原始影像將依處理方法生成多個對應的影像，例如：`lenna-gamma-bel.bmp`、`lenna-grayscale.bmp` 和 `lenna-negative.bmp` 等等。
