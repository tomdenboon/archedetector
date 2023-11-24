<template>
  <div class="w-100">
    <div class="w-100 bg-white" v-if="issueSelectedIdx!==-1">
      <div class="d-flex justify-content-between border-bottom" id="mail-list-header" >
        <div class="px-1 d-flex align-items-center">
          <b-dropdown size="lg"  variant="link" toggle-class="text-decoration-none" no-caret>
            <template #button-content>
              <b-icon-tag></b-icon-tag>
            </template>
            <b-dropdown-form id="dropdown-form" text="Dropdown with form" ref="dropdown">
              <b-form-group>
                <b-form-checkbox-group
                    id="checkbox-group-2"
                    name="flavour-2"
                    v-model="page.content[issueSelectedIdx].tags">
                  <b-form-checkbox v-for="tag in tags" :key="tag.id" :value="tag">{{tag.name}}</b-form-checkbox>
                </b-form-checkbox-group>
                <b-button variant="primary" size="sm" @click="saveIssue">Save</b-button>
              </b-form-group>
            </b-dropdown-form>
          </b-dropdown>
          <div class="p-1" v-for="tag in page.content[issueSelectedIdx].tags" :key="tag.id">
            <div class="p-1 shadow-sm rounded bg-light border" style="user-select: none;">
              {{tag.name}}
            </div>
          </div>
        </div>
        <div class="my-auto" style="user-select: none">
          <b-icon-arrow-left id="icon" v-on:click="issueSelectedIdx = -1" scale="1.5"></b-icon-arrow-left>
          {{page.number*page.size+this.issueSelectedIdx+1}} of {{ page.totalElements }}
          <b-icon-caret-left-fill style="color: grey" class="mx-lg-3" scale="1.5" v-if="page.first && issueSelectedIdx===0"></b-icon-caret-left-fill>
          <b-icon-caret-left-fill id="icon" v-on:click="setSelectedIssue(issueSelectedIdx-1)" class="mx-lg-3" scale="1.5" v-else></b-icon-caret-left-fill>
          <b-icon-caret-right-fill style="color: grey" class="mx-lg-5" scale="1.5" v-if="page.last && issueSelectedIdx+1===page.numberOfElements"></b-icon-caret-right-fill>
          <b-icon-caret-right-fill id="icon" v-on:click="setSelectedIssue(issueSelectedIdx+1)" class="mx-lg-5" scale="1.5" v-else></b-icon-caret-right-fill>
        </div>
      </div>
      <div class="overflow-auto rounded-0" id="mail-view">
        <div class="m-4">
          <b>{{ page.content[issueSelectedIdx].key }}</b>
        </div>
        <div class="m-4">
          <h4>
            {{ page.content[issueSelectedIdx].summary }}
          </h4>
        </div>
        <div class="m-4" style="overflow-x: auto; white-space: pre-line;">
           {{ page.content[issueSelectedIdx].description }}
        </div>
        <b class="m-4"> Comments: </b>
        <div class="m-4" v-for="comment in issueSelectedComments" :key="comment.id">
          <b-card header-tag="header" class="w-100">
            <template #header>
              <div class="d-flex justify-content-between">
                <div>
                  <b> Author: </b>
                  {{comment.author}}
                </div>
                <div>
                  <b>Date:</b> {{ moment.unix(comment.date).format("DD MMM YYYY hh:mm a") }}
                </div>
              </div>
            </template>
            <pre class="w-100" style="white-space: pre-line;">
              {{ comment.body }}
            </pre>
          </b-card>
        </div>
      </div>
    </div>
    <div class="w-100 bg-white" v-else>
      <div class="d-flex justify-content-between border-bottom p-1" id="mail-list-header" >
        <div class="d-flex">
          <b-dropdown size="sm" variant="outline-primary" :text="'Sort by ' + this.sortFields[selectedSortField].displayName">
            <b-dropdown-item v-for="(field, index) in this.sortFields"
                             :key="index"
                             @click="setSortBy(index)">
              {{field.displayName}}
            </b-dropdown-item>
          </b-dropdown>
          <b-button class="mx-2" size="sm" variant="outline-primary" @click="setSortDirection">
            <b-icon-arrow-up v-if="sortDirection === 'asc'"></b-icon-arrow-up>
            <b-icon-arrow-down v-else></b-icon-arrow-down>
          </b-button>
        </div>
        <div class="my-auto" style="user-select: none">
          {{page.number*page.size+1}}-{{page.number*page.size + page.numberOfElements}} of {{ page.totalElements }}
          <b-icon-caret-left-fill style="color: grey" class="mx-lg-3" scale="1.5" v-if="page.first"></b-icon-caret-left-fill>
          <b-icon-caret-left-fill id="icon" @click="prevPage" class="mx-lg-3" scale="1.5" v-else></b-icon-caret-left-fill>
          <b-icon-caret-right-fill style="color: grey" class="mx-lg-5" scale="1.5" v-if="page.last"></b-icon-caret-right-fill>
          <b-icon-caret-right-fill id="icon" @click="nextPage" class="mx-lg-5" scale="1.5" v-else></b-icon-caret-right-fill>
        </div>
      </div>
      <b-list-group class="overflow-auto rounded-0" id="mail-list" flush>
        <b-list-group-item
            @click="setSelectedIssue(index)"
            action
            v-for="(issue, index) in page.content" :key="issue.id"
            id="list-style">
          <div>
            {{ issue.key }}
          </div>
          <div>
            {{ issue.summary }}
          </div>
          <div class="d-flex flex-row" v-if="issue.tags.length > 0">
            <div v-for="tag in issue.tags" :key="tag.id">
              <div class="me-2 p-1 shadow-sm rounded border bg-light" style="user-select: none;">
                {{tag.name}}
              </div>
            </div>
          </div>
        </b-list-group-item>
      </b-list-group>
    </div>
  </div>
</template>

<script>
import axios from "axios";


const url = process.env.VUE_APP_ARCHEDETECOR_API

export default {
  name: "IssueBrowser",
  data(){
    return{
      issueSelectedComments: [],
      issueSelectedIdx: -1,
      tags: [],
      page: {},
      sortDirection: 'desc',
      sortFields: [
        {
          displayName: 'Date',
          name: 'date'
        },
        {
          displayName: 'Tag Count',
          name: 'tagCount'
        }
      ],
      selectedSortField: 0
    }
  },
  mounted() {
    axios.get(url+"tag").then((response) => {
      this.tags=response.data;
    })
    this.getCurrentData()
  },
  methods: {
    apiGetIssues(apiUrl){
      axios.get(apiUrl).then((response) => {
        this.page = response.data;
        if(this.issueSelectedIdx !== -1){
          axios.get( url+ "issue/" + this.page.content[this.issueSelectedIdx].id + "/comment?sort=date").then((response) => {
            this.issueSelectedComments = response.data;
          }, (error) => {
            console.log(error);
          });
        }
        console.log(this.page)
      }, (error) => {
        console.log(error);
      });
    },
    saveIssue(){
      axios.post(url + "issue", this.page.content[this.issueSelectedIdx]).then(() => {
      }, (error) => {
        console.log(error);
      });
    },
    setSelectedIssue(index){
      if(index >= this.page.size){
        this.issueSelectedIdx=0
        this.nextPage()
      } else if(index < 0){
        this.issueSelectedIdx=19
        this.prevPage()
      }else {
        axios.get(url + "issue/" + this.page.content[index].id + "/comment?sort=date").then((response) => {
          this.issueSelectedComments = response.data;
          console.log(this.issueSelectedComments)
        }, (error) => {
          console.log(error);
        });
        this.issueSelectedIdx = index;
      }
    },
    nextPage(){
      const page_nr = this.page.number + 1;
      if (this.$route.params.query) {
        this.$router.push({ name: 'IssueSearch', params: { id: this.$route.params.id, page: page_nr , query: this.$route.params.query}})
      } else {
        this.$router.push({ name: 'Issue', params: { id: this.$route.params.id, page: page_nr }})
      }

    },
    prevPage() {
      const page_nr = this.page.number - 1;
      if (this.$route.params.query) {
        this.$router.push({
          name: 'IssueSearch',
          params: {id: this.$route.params.id, page: page_nr, query: this.$route.params.query}
        })
      } else {
        this.$router.push({name: 'Issue', params: {id: this.$route.params.id, page: page_nr}})
      }
    },
    setSortBy(index){
      this.selectedSortField = index
      this.getCurrentData();
    },
    setSortDirection(){
      if(this.sortDirection === 'asc'){
        this.sortDirection = 'desc'
      } else {
        this.sortDirection = 'asc'
      }
      this.getCurrentData();
    },
    getCurrentData(){
      if(this.$route.params.query){
        let str = ""
        if(this.$route.params.id === 'all') {
          axios.get(url + "query-collection/" + this.$route.params.queryCollectionId).then(response => {
            let ids = ""
            let i = 0
            for(; i < response.data.issueLists.length-1; i++){
              ids += response.data.issueLists[i].id + ","
            }
            ids += response.data.issueLists[i].id
            str = url + "issue/search?q=" + this.$route.params.query + "&issueListIds=" + ids + "&page=" + this.$route.params.page + "&size=20"
            this.apiGetIssues(str)
          })
        } else {
          str = url + "issue/search?q=" + this.$route.params.query + "&issueListIds=" + this.$route.params.id + "&page=" + this.$route.params.page + "&size=20"
          this.apiGetIssues(str)
        }
      } else {
        if(this.$route.params.id === 'all') {
          this.apiGetIssues(url + "query-collection/" + this.$route.params.queryCollectionId + "/issue?page=" + this.$route.params.page +
              "&sort="+ this.sortFields[this.selectedSortField].name + "," + this.sortDirection);
        }else{
          this.apiGetIssues(url + "issue-list/" + this.$route.params.id + "/issue?page=" + this.$route.params.page +
              "&sort=" + this.sortFields[this.selectedSortField].name + "," + this.sortDirection);
        }
      }
    }
  },

  watch:{
    '$route.params': {
      handler: function() {
        this.getCurrentData();
      },
      deep: true
    }
  }

}
</script>

<style scoped>
#mail-list-header{
  height: 40px;
}
#mail-list{
  height: calc(100vh - (70px + 40px));
  width: calc(100vw - 300px);
}
#mail-view{
  padding: 10px;
  height: calc(100vh - (70px + 40px));
  width: calc(100vw - 300px);
}
#icon:hover{
  color: cornflowerblue;
}

</style>