package com.example.weatherapplication


import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class DemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        CallApiLoginAsyncTask().execute()
    }

    private inner class CallApiLoginAsyncTask() : AsyncTask<Any, Void, String>() {

        private lateinit var waitProgressDialog : Dialog

        /**
         * This function is for the task which we wants to perform before background execution.
         * Here we have shown the progress dialog to user that UI is not freeze but executing something in background.
         */
        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        /**
         * This function will be used to perform background execution.
         */
        override fun doInBackground(vararg params: Any?): String {
            // we have to return a String from this Function, so we will return String "result"
            var result : String

            /**
             * https://developer.android.com/reference/java/net/HttpURLConnection
             * The above url for Detail understanding of HttpURLConnection class
             */
            var connection : HttpURLConnection? = null

            try {
                var url = URL("http://www.mocky.io/v2/5e3826143100006a00d37ffa")
                /**
                * The openConnection() method of URL class opens the connection to specified URL and URLConnection instance
                * that represents a connection to the remote object referred by the URL.
                * */
                connection = url.openConnection() as HttpURLConnection // we converted the URLConnection to HttpURLConnection

                /**
                 * A URL connection can be used for input and/or output.  Set the DoOutput
                 * flag to true if you intend to use the URL connection for output,
                 * false if not.  The default is false.
                 */
                connection.doInput = true // to get data
                connection.doOutput = true // to send data

                // Here we are storing the response code we get from the HTTP response
                val httpResponseCode : Int = connection.responseCode

                // There are other response codes also do check going to HttpURLConnection.class
                // HTTP Status-Code 200: OK.
                if(httpResponseCode ==  HttpURLConnection.HTTP_OK){
                    /**
                     * Returns an input stream that reads from this open connection.
                     * follow Link -> https://is.gd/GkG9FW
                     * Reads the Inputstream from the Connection
                     * The data or ordered sequence of bytes are read from a file, received over the network
                     */
                    val inputStream = connection.inputStream

                    /**
                     * We can use reader in order to read every single line
                     * Follow the Link -> https://is.gd/FKT4vT
                     * An "InputStreamReader" is a bridge from byte streams to character streams:
                     * It reads bytes and decodes them into characters using a specified charset.
                     * The charset that it uses may be specified by name or may be given explicitly,
                     * or the platform's default charset may be accepted.
                     * InputStreamReader is the subClass of Reader
                     * It converts the bytes stream to Character Stream
                     * Java "BufferedReader" class is used to read the text from a character-based input stream.
                     * It can be used to read data line by line by readLine() method. It makes the performance fast.
                     * -------
                     * Creates a buffering character-input stream that uses a default-sized input buffer.
                     * */
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    /**
                    * StringBuilder class is used to create a mutable string and it is not thread safe so
                    * multiple thread can access string builder class at a time.
                    * */
                    val stringBuilder = StringBuilder()
                    var line : String? = null
                    try {
                        /**
                         * Reads a line of text.  A line is considered to be terminated by any one
                         * of a line feed ('\n'), a carriage return ('\r'), or a carriage return
                         * followed immediately by a linefeed.
                         */
                        /**
                         * Here the "it" is the result from the readLine and we add the value of "it"
                         * to the 'line' variable and use it afterwards
                         * */
                        while( reader.readLine().also { line = it } != null )
                        {
                            stringBuilder.append(line + "\n")
                        }
                    }
                    catch (e : IOException)
                    {
                        e.printStackTrace()
                    }
                    finally
                    {
                        try
                        {
                            // Closes this input stream and releases any system resources associated with the stream.
                            inputStream.close()
                        }
                        catch (e: IOException)
                        {
                            e.printStackTrace()
                        }
                    }
                    result = stringBuilder.toString()
                } // if Condition Ends here - Status Ok If Condition.
                else
                {
                    result = connection.responseMessage
                }
            }
            catch (e : Exception)
            {
                result = "Error" + e.message
                Log.e("DoInBackground", "Error: " + e.message)
            }
            catch ( e : SocketTimeoutException)
            {
                result = "Connection Timeout"
                Log.e("DoInBackground", "SocketTimeoutException: " + e.message)
            }
            finally {
                connection?.disconnect()
            }

            return result
        }

        /**
         * This function will be executed after the background execution is completed.
         */
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            cancelDialog()

            //Log.i("JSON Response Result", result)

            // Follow the Link -> https://www.baeldung.com/java-org-json
            // The passed String argument must be a valid JSON; otherwise, this constructor may throw a JSONException.
            var jsonObject = JSONObject(result)

            // Calling this function to log the JSON values which we get from the web.
            logJSON(jsonObject)
        }

        // The function to show the Dialog
        private fun showProgressDialog() {
            waitProgressDialog = Dialog(this@DemoActivity)
            waitProgressDialog.setContentView(R.layout.progress_dialog)
            waitProgressDialog.show()
        }
        // The function to cancel the Dialog
        private fun cancelDialog() {
            waitProgressDialog.cancel()
        }

        // This function is to Log The Get JSON object values using Keys
        private fun logJSON(json : JSONObject){
            // getting the value via keys - optValueTypes the value types vary accordingly
            val message = json.optString("message") // string value type
            val user_id = json.optInt("user_id") // int value type
            val email = json.optString("email") // string value type
            val mobile = json.optInt("mobile") // int value type
            val profileObj = json.optJSONObject("profile_details") // object value type
            val profileKey_profileCompleter = profileObj.optBoolean("is_profile_completed") // boolean type
            val profileKey_rating = profileObj.optInt("rating") // int type
            val jsonArray = json.optJSONArray("data_list") // array to say - value type

            Log.i("JSON", "Message: $message")
            Log.i("JSON", "User_ID: $user_id")
            Log.i("JSON", "Email: $email")
            Log.i("JSON", "Mobile: $mobile")
            Log.i("JSON", "ProfileObj-ProfileCompleter: ${profileKey_profileCompleter}")
            Log.i("JSON", "ProfileObj-ProfileRating: ${profileKey_rating}")



            // calling the function to Log JSONArray
            logJSONArray(jsonArray)
        }

        // This function is to log the JSONArray
        private fun logJSONArray(jsonArray : JSONArray){
            // the JSONArray have JSON or to say objects
            for(item in 0 until jsonArray.length()){
                val arrayObj : JSONObject = jsonArray[item] as JSONObject

                Log.i("JSON-Loop", "ID: ${arrayObj.optInt("id")}")
                Log.i("JSON-Loop", "Value: ${arrayObj.optInt("value")}")
            }
        }
    }

}