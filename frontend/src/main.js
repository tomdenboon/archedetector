import Vue from 'vue'
import App from './App.vue'
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'
import moment from 'moment'
import VueRouter from "vue-router";

import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

// Make BootstrapVue available throughout your project

Vue.use(BootstrapVue)
// Optionally install the BootstrapVue icon components plugin
Vue.use(IconsPlugin)
Vue.use(VueRouter)

Vue.config.productionTip = false
Vue.prototype.moment = moment

import Home from './components/Home'
import QueryCollection from "@/components/QueryCollection";
import Mailbox from "@/components/Mailbox";
import IssueBrowser from "@/components/IssueBrowser";
import ManageMailingList from "@/components/manage/ManageMailingList";
import ManageIssueList from "@/components/manage/ManageIssueList";
import ManageData from "@/components/manage/ManageData";
import ManageTag from "@/components/manage/ManageTag";
import ManageQueryCollection from "@/components/manage/ManageQueryCollection";
import ThreadBox from "@/components/ThreadBox";


const routes = [
    {
        name: "Home",
        path: '/',
        component: Home,
    },
    {
        name: "Manage",
        path: '/manage',
        component: ManageData,
        children: [
            {
                name: "ManageIssueList",
                path: 'issue-list',
                component: ManageIssueList,
            },
            {
                name: "ManageMailingList",
                path: 'mailing-list',
                component: ManageMailingList,
            },
            {
                name: "ManageQueryCollection",
                path: 'query-collection',
                component: ManageQueryCollection,
            },
            {
                name: "ManageTag",
                path: 'tag',
                component: ManageTag,
            }
        ]
    },
    {
        name: "QueryCollection",
        path: "/query-collection/:queryCollectionId",
        component: QueryCollection,
        children: [
            {
                name: "Mail",
                path: "mailing-list/:id/email/page/:page",
                component: Mailbox
            },
            {
                name: "MailSearch",
                path: "mailing-list/:id/email/search/:query/page/:page",
                component: Mailbox
            },
            {
                name: "Thread",
                path: "mailing-list/:id/thread/page/:page",
                component: ThreadBox
            },
            {
                name: "ThreadSearch",
                path: "mailing-list/:id/thread/search/:query/page/:page",
                component: ThreadBox
            },
            {
                name: "Issue",
                path: "issue-list/:id/page/:page",
                component: IssueBrowser
            },
            {
                name: "IssueSearch",
                path: "issue-list/:id/search/:query/page/:page",
                component: IssueBrowser
            }
        ]
    },
    {
        path: '*',
        redirect: '/'
    }
]

const router = new VueRouter({
  mode: 'history',
  routes
})

new Vue({
  router,
  render: h => h(App),
}).$mount('#app')
