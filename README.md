<p align="center">
<img src="https://github.com/user-attachments/assets/77940e48-dfe7-472b-b92c-e2dd6a14bbce" alt="The logo of the location share app" width=20% />

<h1 align="center">Location Share</h1>
<p align="center">Simple and reliable location sharing.</p>

<p align="center">
<img alt="GitHub Actions Workflow Status" src="https://img.shields.io/github/actions/workflow/status/Importantus/location-share/build.yaml">
<img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/Importantus/location-share?include_prereleases">
<img alt="GitHub" src="https://img.shields.io/github/license/Importantus/location-share">
</p>

> [!WARNING]
> This app is still under heavy development and not ready for production use!

Location Share is a simple app that allows you to share your location with your friends and family. It is designed to be easy to use, yet open and reliable. The app is built with Jetpack Compose and uses a [self-hostable server](https://github.com/Importantus/location-share-backend) written in Go for the backend.

<img alt="A mockup of the location share app" src="https://github.com/user-attachments/assets/cb62a59b-1635-47ef-b3d3-ff629df05677">

## Why Location Share?
Your location is among your most sensitive data and you should be in control of it. Existing location sharing apps often require you to create an account and share your data with a third party. Location Share is designed to be self-hosted, meaning you can run your own server and be in control of your data.

Being in control of your own data also means that you can do with it whatever you want. The API is open and easy to use, so you can build your own clients, dashboards, run data analysis on your location data, or even integrate it with your smart home.

## What does reliable mean?
As this is just a side project and I am the only developer, I can't guarantee that the app will be updated regularly or be bug-free. However, by reliable, I mean that when you share your location with someone, you want them to always have the latest location. This is why I use the same trick as Google Maps: The app sends a push notification to the device to wake it up and fetch the latest location.

## Development


### Release 

When you want to create a new release, follow these steps:

1. Update the `versionName` (e.g. 1.2.3) and increase the `versionCode` (e.g. 3) in `app/build.gradle.kts` 
1. Commit that change (`git commit -am v1.2.3`)
1. Tag the commit (`git tag v1.2.3`). Make sure your tag name's format is `v*.*.*`
1. Push the changes to GitHub (`git push && git push --tags`)
1. Edit and publish the release draft created by the workflow in GitHub

After building successfully, the action will publish the release artifacts in a new release draft that will be created on GitHub with download links for the app. 


### Contributing
I am happy about every contribution to this project. If you have an idea for a new feature, found a bug, or just want to help out, feel free to open an issue or a pull request. Please make sure to use the gitmoji convention for your commit messages.

## Thanks
This project would not be possible without the following projects:
- [Ramani Maps](https://github.com/ramani-maps/ramani-maps) for the just simple enough compose-wrapper around Maplibre
- [Versatiles](https://versatiles.org/) for the best tile server (and much more) out there
- [Lucide](https://lucide.dev/) for the beautiful icons