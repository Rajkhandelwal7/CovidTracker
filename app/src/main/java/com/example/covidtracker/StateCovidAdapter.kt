package com.example.covidtracker

import android.text.Spannable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.item_list.view.*

class StateCovidAdapter(val list:List<StatewiseItem>):BaseAdapter() {
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]

    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view=convertView?:LayoutInflater.from(parent?.context).inflate(R.layout.item_list,parent,false)
        val item=list[position]
       /* view.cnfrmdtv.text=Spannabledelta("${item.confirmed}}\n↑${item.deltaconfirmed?:"0"}",R.color.dark_red,item.confirmed?.length?:0)
        view.activetv.text=item.active
        view.recvredtv.text=item.recovered
        view.deceasedtv.text=item.deaths
        view.statetv.text=item.state*/
        view.cnfrmdtv.apply {
            text = Spannabledelta(
                "${item.confirmed}\n ↑ ${item.deltaconfirmed ?: "0"}",
                "#D32F2F",
                item.confirmed?.length ?: 0
            )
        }
        view.activetv.text = Spannabledelta(
            "${item.active}\n ↑ ${item.deltaactive ?: "0"}",
            "#1976D2",
            item.confirmed?.length ?: 0
        )
        view.recvredtv.text = Spannabledelta(
            "${item.recovered}\n ↑ ${item.deltarecovered ?: "0"}",
            "#388E3C",
            item.recovered?.length ?: 0
        )
        view.deceasedtv.text = Spannabledelta(
            "${item.deaths}\n ↑ ${item.deltadeaths ?: "0"}",
            "#FBC02D",
            item.deaths?.length ?: 0
        )
        view.statetv.text = item.state

        return view

    }
}