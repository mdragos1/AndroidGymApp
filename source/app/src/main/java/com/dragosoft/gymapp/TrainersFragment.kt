package com.dragosoft.gymapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.databinding.FragmentTrainersBinding
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TrainersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrainersFragment : Fragment() {
    private lateinit var binding:FragmentTrainersBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var adapter: TrainersAdapter

    private var trainersList = ArrayList<TrainersData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trainersList.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTrainersBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerView
        searchView = binding.searchView

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        addDataToList()

        adapter = TrainersAdapter(trainersList){
            t->shareInfo(t)
        }
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })
        return binding.root
    }

    private fun shareInfo(trainer: TrainersData) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra("Share this", "${trainer.name} gym trainer\n Call now at: ${trainer.phone}")

        val chooser = Intent.createChooser(intent, "Share using...")
        startActivity(chooser)
    }

    private fun filterList(newText: String?) {
        if (newText != null){
            val filteredList = ArrayList<TrainersData>()
            for (i in trainersList){
                if (i.name.lowercase(Locale.ROOT).contains(newText)){
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()){
                Toast.makeText(context, "No Trainer found", Toast.LENGTH_SHORT).show()
            }else{
                adapter.setFilteredList(filteredList)
            }
        }
    }

    private fun  addDataToList() {
        trainersList.clear()
        val names = arguments?.getStringArrayList("names")
        val emails = arguments?.getStringArrayList("emails")
        val phones = arguments?.getStringArrayList("phones")
        val photos = arguments?.getStringArrayList("photos")

        for (i in names!!.indices){
            val trainer = TrainersData(names[i], emails!![i], phones!![i], photos!![i])
            trainersList.add(trainer)
        }
    }

    companion object {
        fun newInstance(nameList: ArrayList<String>, emailList: ArrayList<String>,
            phoneList: ArrayList<String>, photoList: ArrayList<String>):TrainersFragment{

            val fragment = TrainersFragment()
            val bundle = Bundle().apply {
                putStringArrayList("names", nameList)
                putStringArrayList("emails", emailList)
                putStringArrayList("phones", phoneList)
                putStringArrayList("photos", photoList)
            }

            fragment.arguments = bundle

            return fragment
        }
    }
}