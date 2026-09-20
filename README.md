# Chốt Chữ - Android (Jetpack Compose)

## Cách 1: Android Studio (dễ nhất)
1. Mở Android Studio -> File -> Open -> chọn thư mục `ChotChu`
2. Đợi Gradle sync xong (lần đầu tải khoảng 5-10 phút)
3. Build -> Build Bundle(s)/APK(s) -> Build APK(s)
4. APK nằm ở: `app/build/outputs/apk/debug/app-debug.apk`

## Cách 2: Dòng lệnh
Cần JDK 17 + Android SDK (ANDROID_HOME đã set), rồi chạy:

    gradle wrapper
    ./gradlew assembleDebug

## Cách 3: Không cần cài gì - build trên GitHub
1. Tạo repo mới trên GitHub, đẩy toàn bộ thư mục này lên
2. Tab **Actions** -> workflow "Build APK" tự chạy
3. Tải APK ở mục **Artifacts** của lần chạy đó

## Cài lên điện thoại
Copy `app-debug.apk` vào máy, bật "Cài đặt từ nguồn không xác định" rồi mở file.
