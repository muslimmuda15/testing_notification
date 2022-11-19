package com.rachmad.training.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.navigation.fragment.findNavController
import com.google.firebase.messaging.RemoteMessage
import com.rachmad.training.notification.databinding.FragmentFirstBinding
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            sendNotification("Notif", "Notif description", "7")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sendNotification(title: String, message: String, id: String) {
        // use constant ID for notification used as group summary
        val notif = NotificationCompat.InboxStyle()
        val intent: Intent?
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_baseline_circle_notifications_24)

        notif
            .addLine(title)
            .setBigContentTitle("My notif title")
            .setSummaryText("New order")

//        if(!BaseActivity.doSplash) {
//            intent = packageManager.getLaunchIntentForPackage(BuildConfig.APPLICATION_ID)?.apply {
//                putExtra(ORDER_ID, message.data["oid"])
//                putExtra(ORDER_TYPE_FLAG, Argument.FIREBASE_FLAG)
//                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            }
//        } else {
        intent = Intent(context, MainActivity::class.java)

//        }

        val resultPendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE
        )

        val notificationManager = context?.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager

        /**
         * Notification Channel
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(id, "Appety Notification", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "This is appety notification channel"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 500)

            notificationManager.createNotificationChannel(notificationChannel)
        }

        val time = Date(System.currentTimeMillis())

        val messageNotification = NotificationCompat.Builder(requireContext(), id)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_baseline_message_24)
//            .setStyle(notificationRepository.inboxStyle)
            .setLargeIcon(bitmap)
            .setContentTitle("new message at ${time.hours}:${time.minutes}")
            .setContentText("${message}")

        /**
         * Main Notification
         */
        val summaryNotification = NotificationCompat.Builder(requireContext(), id)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setStyle(
                NotificationCompat.InboxStyle()
                    .setSummaryText("Incoming orders")
            )
            .setSmallIcon(R.drawable.ic_baseline_message_24)
            .setLargeIcon(bitmap)
            .setContentTitle("new message at ${time.hours}:${time.minutes}")
            .setContentText("${message}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            summaryNotification.setGroup(BuildConfig.APPLICATION_ID)
            messageNotification.setGroup(BuildConfig.APPLICATION_ID)
            summaryNotification.setGroupSummary(true)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), messageNotification.build())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            notificationManager.notify(1, summaryNotification.build())

    }
}