<template>
  <nav class="navbar justify-content-between" id="bar-style">
    <a class="ms-2 navbar-brand" @click="navigateHome" style="cursor: pointer">Archedetector</a>
    <form class="d-flex w-50 align-items-center" v-on:submit.prevent>
      <input class="w-50 mx-1" v-model="searchQuery" placeholder="Search" v-on:keydown.enter="search">
      <b-button size="sm" @click="search">submit</b-button>
      <b-button v-if="this.$route.name==='IssueSearch' || this.$route.name==='MailSearch' || this.$route.name==='ThreadSearch' " class="m-1" size="sm" variant="primary" @click="exportQuery">
        Export to json
      </b-button>
    </form>
    <img class="img-responsive"
         src="@/assets/logo-groningen.svg"
         alt="" />
  </nav>
</template>

<script>
import axios from "axios";

const url = process.env.VUE_APP_ARCHEDETECOR_API

export default {
  name: "navbar",
  data(){
    return{
      searchQuery: "",
    }
  },
  methods: {
    search(){
      let q = this.searchQuery.replaceAll("+", "%2B")
      console.log(this.$route.name)
      if(this.$route.name === "Issue" || this.$route.name === "IssueSearch") {
        if(q==="") {
          this.$router.push({name: 'Issue', params: {id: this.$route.params.id, page: 0}})
        } else {
          this.$router.push({name: 'IssueSearch', params: {id: this.$route.params.id, query: q, page: 0}})
        }
      } else if(this.$route.name === "Mail" || this.$route.name === "MailSearch"){
        if(q==="") {
          this.$router.push({name: 'Mail', params: {id: this.$route.params.id, page: 0}})
        } else {
          this.$router.push({name: 'MailSearch', params: {id: this.$route.params.id, query: q, page: 0}})
        }
      } else {
        if(q==="") {
          this.$router.push({name: 'Thread', params: {id: this.$route.params.id, page: 0}})
        } else {
          this.$router.push({name: 'ThreadSearch', params: {id: this.$route.params.id, query: q, page: 0}})
        }
      }

    },
    navigateHome(){
      this.$router.push({name:"Home"});
    },
    exportQuery() {
      let q = this.$route.params.query.replaceAll("%2B", "+")
      if (this.$route.name === "MailSearch") {
        if(this.$route.params.id === "all") {
          axios.get(url + "query-collection/" + this.$route.params.queryCollectionId).then(response => {
            let ids = ""
            let i = 0
            for (; i < response.data.mailingLists.length - 1; i++) {
              ids += response.data.mailingLists[i].id + ","
            }
            ids += response.data.mailingLists[i].id
            axios.get(url + "email/export?q=" + this.$route.params.query + "&mailingListIds=" + ids).then(response => {
              this.saveFile("emailQuery.json", {
                query: q,
                resultSize: response.data.length,
                emails: response.data
              })
            })
          })
        } else {
          axios.get(url + "email/export?q=" + this.$route.params.query + "&mailingListIds=" + this.$route.params.id).then(response => {
            this.saveFile("emailQuery.json",{
              query: q,
              resultSize: response.data.length,
              emails: response.data
            })
          })
        }
      } else if (this.$route.name === "IssueSearch"){
        if(this.$route.params.id === "all") {
          axios.get(url + "query-collection/" + this.$route.params.queryCollectionId).then(response => {
            let ids = ""
            let i = 0
            for (; i < response.data.issueLists.length - 1; i++) {
              ids += response.data.issueLists[i].id + ","
            }
            ids += response.data.issueLists[i].id
            axios.get(url + "issue/export?q=" + this.$route.params.query + "&issueListIds=" + ids).then(response => {
              this.saveFile("issueQuery.json", {
                query: q,
                resultSize: response.data.length,
                issues: response.data
              })
            })
          })
        } else {
          axios.get(url + "issue/export?q=" + this.$route.params.query + "&issueListIds=" + this.$route.params.id).then(response => {
            this.saveFile("issueQuery.json", {
              query: q,
              resultSize: response.data.length,
              issues: response.data
            })
          })
        }
      } else {
        if(this.$route.params.id === "all") {
          axios.get(url + "query-collection/" + this.$route.params.queryCollectionId).then(response => {
            let ids = ""
            let i = 0
            for (; i < response.data.mailingLists.length - 1; i++) {
              ids += response.data.mailingLists[i].id + ","
            }
            ids += response.data.mailingLists[i].id
            console.log(ids)
            axios.get(url + "email-thread/export?q=" + this.$route.params.query + "&mailingListIds=" + ids).then(response => {
              this.saveFile("emailThreadQuery.json", {
                query: q,
                resultSize: response.data.length,
                emailThreads: response.data
              })
            })
          })
        } else {
          axios.get(url + "email-thread/export?q=" + this.$route.params.query + "&mailingListIds=" + this.$route.params.id).then(response => {
            this.saveFile("emailThreadQuery.json", {
              query: q,
              resultSize: response.data.length,
              emailThreads: response.data
            })
          })
        }
      }
    },
    saveFile(fileName, json){
      const data = JSON.stringify(json)
      const blob = new Blob([data], {type: 'text/plain'})
      const e = document.createEvent('MouseEvents'),
          a = document.createElement('a');
      a.download = fileName;
      a.href = window.URL.createObjectURL(blob);
      a.dataset.downloadurl = ['text/json', a.download, a.href].join(':');
      e.initEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
      a.dispatchEvent(e);
    }
  }
}
</script>

<style scoped>
#bar-style{
  height:70px
}
</style>