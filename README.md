# Daily Image App
[![coverage](https://raw.githubusercontent.com/ngengs/daily-image-app-android/badges/develop-jacoco.svg)](https://github.com/ngengs/daily-image-app-android/actions/workflows/build_develop.yml)
[![Develop Build](https://github.com/ngengs/daily-image-app-android/actions/workflows/build_develop.yml/badge.svg?branch=develop)](https://github.com/ngengs/daily-image-app-android/actions/workflows/build_develop.yml)

This is my simple app to show image from [Unsplash](https://unsplash.com/).

#### Feature
- List of latest image
- List of popular image
- Search image
- Search keyword suggestion
- Change layout list / grid
- Detail image
- Dark / Light theme
- Offline cache first 20 photo from the list

### Build
1. Get the API Key.

   To build this project you need [Unsplash](https://unsplash.com/) `API Key`, please read [here](https://unsplash.com/documentation#creating-a-developer-account) for detail to get the `API Key`.

2. Create your `secrets.properties`.

   Or just run this command `cp .secrets.properties secrets.properties`.

3. Put your `API Key` in `UNSPLASH_API_KEY`.
4. Build or run the project.

   With your Android Studio or terminal using `./gradlew assembleDebug`.


**Note:** App will cache the network request to keep the request limit of the api. Release build have 30 minutes cache, debug build have 240 minutes cache.

### Preview

<details>
  <summary>Phone</summary>


- Grid

|Dark|Light|
|----|-----|
|![Grid Preview Dark](/.github/readme-images/preview-phone-grid-dark.png)|![Grid Preview Light](/.github/readme-images/preview-phone-grid-light.png)|

- List

|Dark|Light|
|----|-----|
|![List Preview Dark](/.github/readme-images/preview-phone-list-dark.png)|![List Preview Light](/.github/readme-images/preview-phone-list-light.png)|

- Detail

|Dark|Light|
|----|-----|
|![Detail Preview Dark](/.github/readme-images/preview-phone-detail-dark.png)|![Detail Preview Light](/.github/readme-images/preview-phone-detail-light.png)|

</details>


<details>
  <summary>Tablet</summary>

- Grid

  ![Grid Preview Dark](/.github/readme-images/preview-tablet-grid.png)

- List

  ![List Preview Dark](/.github/readme-images/preview-tablet-list.png)

- Detail

  ![Detail Preview Dark](/.github/readme-images/preview-tablet-detail.png)

</details>