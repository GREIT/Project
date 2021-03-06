package it.polito.mad.greit.project

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.transaction_card.view.*
import java.text.DateFormat
import java.util.*

class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
  fun bindReview(transaction: BookTransaction?, fm: android.support.v4.app.FragmentManager, ctx: Context) {
    with(transaction!!) {
      itemView.transaction_book_title.text = transaction.bookTitle
      if (transaction.ownerUid.equals(FirebaseAuth.getInstance().getCurrentUser()!!.uid)) {
        itemView.transaction_from_to.text = ctx.resources.getString(R.string.to_user)
        itemView.transaction_actor.text = "@" + transaction.receiverUsername
        itemView.transaction_actor.setOnClickListener{view -> run {
          val I = Intent(ctx, OtherProfile::class.java)
          I.putExtra("uid", transaction.receiverUid)
          I.addFlags(FLAG_ACTIVITY_NEW_TASK)
          ctx.startActivity(I)
        }}
        if (transaction.dateEnd == 0L) {
          itemView.transaction_right.setImageResource(R.drawable.ic_triangle_right)
          itemView.transaction_right.visibility = View.VISIBLE
          itemView.transaction_write_review_right.visibility = View.VISIBLE
          itemView.transaction_left.layoutParams.width = 0
          itemView.transaction_left.requestLayout()
        } else {
          itemView.transaction_right.setImageResource(R.drawable.ic_triangle_right)
          itemView.transaction_right.visibility = View.VISIBLE
          itemView.transaction_right.setBackgroundColor(ctx.getResources().getColor(R.color.unavailable))
          itemView.transaction_write_review_right.visibility = View.VISIBLE
          itemView.transaction_left.layoutParams.width = 0
          itemView.transaction_left.requestLayout()
        }
      } else {
        itemView.transaction_from_to.text = ctx.resources.getString(R.string.from_user)
        itemView.transaction_actor.text = "@" + transaction.ownerUsername
        itemView.transaction_actor.setOnClickListener{view -> run {
          val I = Intent(ctx, OtherProfile::class.java)
          I.putExtra("uid", transaction.ownerUid)
          I.setFlags(FLAG_ACTIVITY_NEW_TASK)
          ctx.startActivity(I)
        }}
        if (transaction.dateEnd == 0L) {
          itemView.transaction_left.setImageResource(R.drawable.ic_triangle_right)
          itemView.transaction_left.visibility = View.VISIBLE
          itemView.transaction_write_review_left.visibility = View.VISIBLE
          itemView.transaction_right.layoutParams.width = 0
          itemView.transaction_right.requestLayout()
        } else {
          itemView.transaction_left.setImageResource(R.drawable.ic_triangle_right)
          itemView.transaction_left.visibility = View.VISIBLE
          itemView.transaction_left.setBackgroundColor(ctx.getResources().getColor(R.color.unavailable))
          itemView.transaction_write_review_left.visibility = View.VISIBLE
          itemView.transaction_right.layoutParams.width = 0
          itemView.transaction_right.requestLayout()
        }
      }
      itemView.transaction_start_date_from.text = ctx.resources.getString(R.string.from)
      itemView.transaction_start_date.text = DateFormat.getDateInstance(DateFormat.SHORT).format( Date(transaction.dateStart * 1000))
      if (transaction.dateEnd != 0L) {
        itemView.transaction_end_date_to.text = ctx.resources.getString(R.string.to)
        itemView.transaction_end_date.text = DateFormat.getDateInstance(DateFormat.SHORT).format( Date(transaction.dateEnd * 1000))
      }
      else {
        itemView.transaction_end_date_to.text = ""
        itemView.transaction_end_date.text = ""
      }

      itemView.transaction_write_review_left.setOnClickListener{view -> run {
        val bundle = Bundle()
        if (transaction.ownerUid.equals(FirebaseAuth.getInstance().getCurrentUser()!!.uid)) {
          bundle.putBoolean("owner", true)
          bundle.putString("reviewed_uid", transaction.receiverUid)
          bundle.putString("reviewed_username", transaction.receiverUsername)
        } else {
          bundle.putBoolean("owner", false)
          bundle.putString("reviewed_uid", transaction.ownerUid)
          bundle.putString("reviewed_username", transaction.ownerUsername)
        }
        bundle.putString("chat_id", transaction.chatId)
        bundle.putBoolean("already_reviewed_by_borrower", transaction.alreadyReviewedByBorrower)
        bundle.putBoolean("already_reviewed_by_owner", transaction.alreadyReviewedByOwner)
        bundle.putString("shared_book_title", transaction.bookTitle)
        val dialogFragment = WriteReview()
        dialogFragment.arguments = bundle
        dialogFragment.show(fm, "dialog")
      }}

      itemView.transaction_write_review_right.setOnClickListener{view -> run {
        val bundle = Bundle()
        if (transaction.ownerUid.equals(FirebaseAuth.getInstance().getCurrentUser()!!.uid)) {
          bundle.putBoolean("owner", true)
          bundle.putString("reviewed_uid", transaction.receiverUid)
          bundle.putString("reviewed_username", transaction.receiverUsername)
        } else {
          bundle.putBoolean("owner", false)
          bundle.putString("reviewed_uid", transaction.ownerUid)
          bundle.putString("reviewed_username", transaction.ownerUsername)
        }
        bundle.putString("chat_id", transaction.chatId)
        bundle.putBoolean("already_reviewed_by_borrower", transaction.alreadyReviewedByBorrower)
        bundle.putBoolean("already_reviewed_by_owner", transaction.alreadyReviewedByOwner)
        bundle.putString("shared_book_title", transaction.bookTitle)
        val dialogFragment = WriteReview()
        dialogFragment.arguments = bundle
        dialogFragment.show(fm, "dialog")
      }}

    }
  }
}