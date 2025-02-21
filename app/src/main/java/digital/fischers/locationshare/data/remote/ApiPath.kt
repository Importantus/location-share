package digital.fischers.locationshare.data.remote

enum class ApiPath(val path: String) {
    SESSIONS("/v1/sessions"),
    USERS("/v1/users"),
    LOCATIONS("/v1/locations"),
    SHARES("/v1/shares"),
    SHARED_LOCATIONS("/v1/shared-locations"),
    INFO("/v1/info"),
    REGISTER_FCM_TOKEN("/v1/sessions/register-fcm-token"),
    WAKE_UP("/v1/users/wake-up")
}