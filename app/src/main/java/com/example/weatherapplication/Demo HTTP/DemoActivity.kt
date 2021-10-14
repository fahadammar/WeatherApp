package com.example.weatherapplication


import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.weatherapplication.Model.ResponseModel
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class DemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        CallApiLoginAsyncTask("denis", "1245").execute()
    }

    private inner class CallApiLoginAsyncTask(val username: String, val password: String) : AsyncTask<Any, Void, String>() {

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

                /**
                 * Sets whether HTTP redirects should be automatically followed by this instance.
                 * The default value comes from followRedirects, which defaults to true.
                 */
                connection.instanceFollowRedirects = false

                /**
                 * Set the method for the URL request, one of:
                 *  GET
                 *  POST
                 *  HEAD
                 *  OPTIONS
                 *  PUT
                 *  DELETE
                 *  TRACE
                 *  are legal, subject to protocol restrictions.  The default method is GET.
                 */
                connection.requestMethod = "POST"

                /**
                 * Sets the general request property. If a property with the key already
                 * exists, overwrite its value with the new value.
                 * connection.setRequestProperty(key, value)
                 */
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")


                /**
                 * Some protocols do caching of documents.  Occasionally, it is important
                 * to be able to "tunnel through" and ignore the caches (e.g., the
                 * "reload" button in a browser).  If the UseCaches flag on a connection
                 * is true, the connection is allowed to use whatever caches it can.
                 *  If false, caches are to be ignored.
                 *  The default value comes from DefaultUseCaches, which defaults to
                 * true.
                 */
                connection.useCaches = false


                /**
                 * OutputStream is an abstract class that represents writing output.
                 * There are many different OutputStream classes, and they write out to certain things (like the screen, or Files, or byte arrays, or network connections, or etc).
                 * InputStream classes access the same things, but they read data in from them.
                 * Creates a new data output stream to write data to the specified
                 * underlying output stream. The counter written is set to zero.
                 */
                var writeStream = DataOutputStream(connection.outputStream)

                // create JSON Object
                var jsonPost = JSONObject()
                jsonPost.put("username", username)
                jsonPost.put("password", password)

                /**
                 * Writes out the string to the underlying output stream as a
                 * sequence of bytes. Each character in the string is written out, in
                 * sequence, by discarding its high eight bits. If no exception is
                 * thrown, the counter written is incremented by the
                 * length of s.
                 */
                writeStream.writeBytes(jsonPost.toString())
                writeStream.flush() // Flushes this data output stream.
                // Closes this output stream and releases any system resources associated with the stream
                writeStream.close()

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

            // Map the json response with the Data Class using GSON.
            var responseData = Gson().fromJson(result, ResponseModel::class.java)

            // Calling this function to log the JSON values which we get from the web.
            logGson(responseData)

            // Calling this function to log the JSON values which we get from the web.
            //logJSON(jsonObject)
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
        // This is the Old Way - The new way is Using is Gson
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

        private fun logGson(responseModel : ResponseModel) {
            Log.i("Message", responseModel.message)
            Log.i("User Id", "${responseModel.user_id}")
            Log.i("Name", responseModel.name)
            Log.i("Email", responseModel.email)
            Log.i("Mobile", "${responseModel.mobile}")

            // Profile Details
            Log.i("Is Profile Completed", "${responseModel.profile_details.is_profile_completed}")
            Log.i("Rating", "${responseModel.profile_details.rating}")

            // Data List Details.
            Log.i("Data List Size", "${responseModel.data_list.size}")

            for (item in responseModel.data_list.indices) {
                Log.i("Value $item", "${responseModel.data_list[item]}")

                Log.i("ID", "${responseModel.data_list[item].id}")
                Log.i("Value", "${responseModel.data_list[item].value}")
            }

            Toast.makeText(this@DemoActivity, responseModel.message, Toast.LENGTH_SHORT).show()
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