import express from "express";
import cors from "cors";
import { GoogleGenerativeAI } from "@google/generative-ai";

const app = express();
app.use(cors());
app.use(express.json({limit:"10mb"}));

const port = process.env.PORT || 8080;
const key = process.env.GEMINI_API_KEY;

app.get("/health", (_,res)=>res.json({ok:true, service:"S2B Hayagriva"}));

app.post("/chat", async (req,res)=>{
  try {
    if(!key) return res.status(503).json({error:"GEMINI_API_KEY is not configured"});
    const modelName = process.env.GEMINI_MODEL || "gemini-2.0-flash";
    const ai = new GoogleGenerativeAI(key);
    const model = ai.getGenerativeModel({model:modelName});
    const result = await model.generateContent(String(req.body?.prompt || ""));
    res.json({text: result.response.text()});
  } catch(e) {
    res.status(500).json({error:e.message});
  }
});

app.listen(port, ()=>console.log(`Hayagriva backend listening on ${port}`));
