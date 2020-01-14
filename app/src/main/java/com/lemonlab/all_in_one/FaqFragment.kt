package com.lemonlab.all_in_one


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.faq_item.view.*
import kotlinx.android.synthetic.main.fragment_faq.*

/**
 * A fragment that shows some questions with their answers in a recycler view.
 */
class FaqFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_faq, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val adapter = GroupAdapter<ViewHolder>()
        faq_rv.adapter = adapter

        val faq = resources.getStringArray(R.array.faq)
        val answers = resources.getStringArray(R.array.faqAnswers)
        val allFaqs = faq.zip(answers)

        for (item in allFaqs)
            adapter.add(FAQItem(FAQ(item.first, item.second)))


    }


    data class FAQ(val question: String, val answer: String)

    inner class FAQItem(private val faq: FAQ) : Item<ViewHolder>() {

        override fun getLayout() = R.layout.faq_item

        override fun bind(viewHolder: ViewHolder, position: Int) {

            val view = viewHolder.itemView


            with(view) {
                faq_question.text = faq.question
                faq_answer.text = faq.answer
                listOf(faq_question, faq_answer).forEach {
                    it.setOnClickListener {
                        if (faq_answer.visibility == View.GONE)
                            faq_answer.visibility = View.VISIBLE
                        else
                            faq_answer.visibility = View.GONE
                    }
                }
            }


        }

    }
}


