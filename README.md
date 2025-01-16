# image-processing
## 程式說明：影像處理應用

使用 Java 實作影像處理程式，具備將原圖以灰階、負片、Gamma校正（Gamma correction）、對比拉伸（Contrast stretch）、椒鹽雜訊（Salt-and-pepper noise）、$3\times3$ 中值濾波器（Median filter）、拉普拉斯運算子（Laplace operator, Laplacian）、$3\times3$ 平均值濾波器（Average filter）和 Otsu 二值化（Otsu's method）等等常見影像處理方法。

### **程式結構與功能**

1. **Libraries**：
    - `java.awt.image.BufferedImage`: 用來讀取與處理圖片的像素。
    - `java.io.File`: 處理文件操作。
    - `java.io.IOException`: 處理 I/O 異常。
    - `javax.imageio.ImageIO`: 用於讀取與寫入圖片。
    - `java.util.Arrays`: 提供數組操作方法，例如排序。
    - `java.util.Random`: 用來生成隨機數（例如模擬椒鹽噪聲）。

2. **Functions**：
    - `GrayScale`: 將圖片轉換為灰階。
    - `Negative`: 產生圖片的負片效果。
    - `Contrast (伽瑪校正)`: 使用不同的伽瑪值來調整對比度。
    - `SaltnPepper`: 添加椒鹽噪聲。
    - `Median`: 使用 $3\times3$ 的中值濾波器去除椒鹽噪聲。
    - `Laplacian`: 使用拉普拉斯運算子檢測圖片邊緣。
    - `Max`: 使用 $3\times3$ 的最大濾波器強化亮點。
    - `Otsu`: 使用 Otsu 方法將圖片二值化。

---

### **模組詳解**

#### 1. **GrayScale (灰階轉換)**
將彩色圖片轉換為灰階圖片。
- 將圖片的每個像素顏色分解為紅（R）、綠（G）、藍（B）三個分量。
- 使用平均值方法計算灰階值：

    ```math
    {Gray} = \frac{R + G + B}{3}
    ```

- 將灰階值更新到圖片中並保存。

---

#### 2. **Negative (負片效果)**
生成圖片的負片。
- 將每個像素的 RGB 值從 255 中減去，產生負片效果：

    ```math
    R_{new} = 255 - R, \quad G_{new} = 255 - G, \quad B_{new} = 255 - B
    ```

- 保存處理後的圖片。

---

#### 3. **Contrast (伽瑪校正)**
通過伽瑪值調整圖片對比度。
- 使用公式進行伽瑪校正：

    ```math
    I_{out} = \left(\frac{I_{in}}{255}\right)^\gamma \times 255
    ```

- 根據不同的伽瑪值 (`gamma > 1`、`gamma = 1`、`gamma < 1`)，調整亮度與對比度。
- 處理並保存不同伽瑪值的結果。

---

#### 4. **SaltnPepper (椒鹽噪聲)**
添加隨機的黑白點模擬椒鹽噪聲。
- 使用隨機數生成器決定像素是否受影響。
- 50% 的機率將像素設為黑色（0） 或白色（255）。

---

#### 5. **Median (中值濾波器)**
去除椒鹽噪聲，平滑圖片。
- 使用 $3\times3$ 的滑動窗口提取每個像素的鄰域。
- 將鄰域像素值排序，選取中間值作為新的像素值。

1. 每個像素 $(i, j)$ 的鄰域是一個 $3\times3$ 的矩陣：

    ```math
    {Neighborhood} =
    \begin{bmatrix}
    P_{i-1, j-1} & P_{i-1, j} & P_{i-1, j+1} \\
    P_{i, j-1} & P_{i, j} & P_{i, j+1} \\
    P_{i+1, j-1} & P_{i+1, j} & P_{i+1, j+1}
    \end{bmatrix}
    ```

2. 將鄰域內的所有像素值排成一維數組並排序：

    ```math
    {Sorted Array} = [P_{min}, \dots, P_{median}, \dots, P_{max}]
    ```

3. 取中間值 $P_{median}$ 作為新的像素值，更新圖片的該像素位置：

    ```math
    P'_{i,j} = P_{median}
    ```

---

#### 6. **Laplacian (拉普拉斯運算子)**
檢測圖片邊緣。
- 使用拉普拉斯模板進行卷積操作：

    ```math
    \begin{bmatrix}
    0 & -1 & 0 \\
    -1 & 4 & -1 \\
    0 & -1 & 0
    \end{bmatrix}
    ```

- 計算每個像素的梯度強度，生成邊緣檢測結果。

1. 使用的 $3\times3$ 拉普拉斯核為：

    ```math
    {Kernel (Laplacian)} =
    \begin{bmatrix}
    0 & -1 & 0 \\
    -1 & 4 & -1 \\
    0 & -1 & 0
    \end{bmatrix}
    ```

2. 對於每個像素位置 $(i, j)$，應用拉普拉斯核進行卷積計算：

    ```math
    P'_{i,j} = (P_{i-1, j} + P_{i+1, j} + P_{i, j-1} + P_{i, j+1}) - 4 \cdot P_{i, j}
    ```

3. 為避免負值，取絕對值：
    ```math
    P'_{i,j} = |P'_{i,j}|
    ```

---

#### 7. **Max (最大濾波器)**
使用 $3\times3$ 的最大濾波器強化圖片中的亮點。
- 提取 $3\times3$ 鄰域的像素，選取最大值作為新像素值。

1. 每個像素 $(i, j)$ 的鄰域同樣是一個 $3\times3$ 的矩陣：
    ```math
    {Neighborhood} =
    \begin{bmatrix}
    P_{i-1, j-1} & P_{i-1, j} & P_{i-1, j+1} \\
    P_{i, j-1} & P_{i, j} & P_{i, j+1} \\
    P_{i+1, j-1} & P_{i+1, j} & P_{i+1, j+1}
    \end{bmatrix}
    ```
2. 將鄰域內的所有像素值提取並找出最大值：
    ```math
    P_{max} = \max(P_{i-1, j-1}, P_{i-1, j}, P_{i-1, j+1}, P_{i, j-1}, P_{i, j}, P_{i, j+1}, P_{i+1, j-1}, P_{i+1, j}, P_{i+1, j+1})
    ```
3. 用該最大值更新圖片該像素位置：
    ```math
    P'_{i,j} = P_{max}
    ```

---

#### 8. **Otsu (Otsu 二值化)**
根據 Otsu 方法將灰階圖片二值化。
- 計算全局閾值（所有像素的平均值）。

    ```math
    {Threshold} = \frac{Sum of all pixel values}{Total number of pixels}
    ```

- 將大於閾值的像素設為白色，其他設為黑色。
