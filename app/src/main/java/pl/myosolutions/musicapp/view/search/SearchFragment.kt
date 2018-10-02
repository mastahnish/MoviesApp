package pl.myosolutions.musicapp.view.search


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import pl.myosolutions.musicapp.R
import pl.myosolutions.musicapp.databinding.FragmentSearchBinding


class SearchFragment : Fragment() {
    companion object {

        private val QUERY_TEXT_KEY: String? = "QUERY_TEXT_KEY"

        fun getInstance(query: String): SearchFragment {
            var frag = SearchFragment()
            var args = Bundle()
            args.putString(QUERY_TEXT_KEY, query)
            frag.arguments = args
            return frag
        }
    }

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        var query = arguments?.getString(QUERY_TEXT_KEY)

        binding.textView.text = when {
            query.equals("", true) -> "Query is null or empty"
            else -> query
        }

        return binding.root
    }


}
