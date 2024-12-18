import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT

object JWTUtils {
    fun getClaim(token: String, claim: String): String? {
        return try {
            val decodedJWT: DecodedJWT = JWT.decode(token)
            decodedJWT.getClaim(claim)?.asString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
