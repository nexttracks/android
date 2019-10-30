
set -ev


./gradlew assembleRelease

if [ "${TRAVIS_TAG}" != "" ]; then
  echo "Tagged with: $TRAVIS_TAG"
  jarsigner -verbose -sigalg SHA1withRSA -storepass $storepass -keypass $keypass -digestalg SHA1 -keystore ../android.jks ./app/build/outputs/apk/release/app-release-unsigned.apk owntracks
  /usr/local/android-sdk/build-tools/29.0.2/zipalign -v 4 app/build/outputs/apk/app-release-unsigned.apk app/build/outputs/apk/app-release.apk
  ls -la app/build/outputs/apk
fi
