package se.berellstudios.app

import PingResponse
import UserLoginRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Context
import android.util.Log
import android.widget.Toast

//Kanske döda denna nu när vi vet att kopplingen funkar?

object APICalls {

    fun callPingApi(context: Context, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        RetrofitClient.apiService.ping().enqueue(object : Callback<PingResponse> {
            override fun onResponse(call: Call<PingResponse>, response: Response<PingResponse>) {
                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "No message"
                    onSuccess(message)
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                } else {
                    val error = "Server Error"
                    onError(error)
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PingResponse>, t: Throwable) {
                val errorMessage = "Network Error: ${t.message}"
                onError(errorMessage)
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                Log.e("Berell", errorMessage)
            }
        })
    }




}
